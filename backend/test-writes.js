// Ad-hoc end-to-end test for the write endpoints. Creates one scoped test user,
// exercises every write op, then deletes ONLY the data tied to that user's id.
require("dotenv").config();
const { connectDB } = require("./src/config/db");

const B = "http://localhost:3000/api";
const j = async (r) => {
  const t = await r.text();
  try { return { status: r.status, body: JSON.parse(t) }; } catch { return { status: r.status, body: t }; }
};
const post = (p, b) => fetch(B + p, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(b) }).then(j);
const put = (p, b) => fetch(B + p, { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify(b) }).then(j);
const del = (p) => fetch(B + p, { method: "DELETE" }).then(j);
const get = (p) => fetch(B + p).then(j);
const ok = (label, cond, extra = "") => console.log(`${cond ? "PASS" : "FAIL"}  ${label}${extra ? "  " + extra : ""}`);

(async () => {
  const email = `qa_${Date.now()}@pompom.vn`;
  const reg = await post("/auth/register", { full_name: "QA Writes", email, password: "123456", phone_number: "0900" });
  const uid = reg.body.id;
  ok("register", reg.status === 201 && uid, `id=${uid}`);

  // change password + verify login with new one
  const cp = await post(`/users/${uid}/change-password`, { old_password: "123456", new_password: "abcdef" });
  ok("change-password", cp.status === 200 && cp.body.ok);
  const login = await post("/auth/login", { email, password: "abcdef" });
  ok("login after change", login.status === 200 && login.body.id === uid);
  const badCp = await post(`/users/${uid}/change-password`, { old_password: "wrong", new_password: "x" });
  ok("change-password rejects wrong old", badCp.status === 400);

  // addresses
  const a1 = await post(`/users/${uid}/addresses`, { label: "Nhà", recipient_name: "A", phone: "0900", address_line: "1 ABC", ward: "P1", district: "Q1", city: "HCM", is_default: true });
  const a2 = await post(`/users/${uid}/addresses`, { label: "Cty", recipient_name: "B", phone: "0901", address_line: "2 XYZ", ward: "P2", district: "Q3", city: "HCM", is_default: true });
  let addrs = (await get(`/users/${uid}/addresses`)).body;
  const defCount = addrs.filter((x) => x.is_default).length;
  ok("add 2 addresses", addrs.length === 2, `count=${addrs.length}`);
  ok("only one default after 2nd default", defCount === 1);
  await put(`/users/${uid}/addresses/${a1.body.id}/default`, {});
  addrs = (await get(`/users/${uid}/addresses`)).body;
  ok("set default switches", addrs.find((x) => x.id === a1.body.id).is_default === true && defCount === 1);
  const da = await del(`/users/${uid}/addresses/${a2.body.id}`);
  ok("delete address", da.body.ok && (await get(`/users/${uid}/addresses`)).body.length === 1);

  // wishlist (idempotent)
  const products = (await get("/products")).body;
  const pid = products[0].id;
  await post(`/users/${uid}/wishlist`, { product_id: pid });
  await post(`/users/${uid}/wishlist`, { product_id: pid }); // duplicate
  let wl = (await get(`/users/${uid}/wishlist`)).body;
  ok("wishlist add idempotent", wl.length === 1, `count=${wl.length}`);
  await del(`/users/${uid}/wishlist/${pid}`);
  wl = (await get(`/users/${uid}/wishlist`)).body;
  ok("wishlist remove", wl.length === 0);

  // cart: add, increment, update qty, get total
  const pid2 = products[1].id;
  await post("/carts/items", { user_id: uid, product_id: pid, quantity: 2 });
  await post("/carts/items", { user_id: uid, product_id: pid, quantity: 3 }); // same -> increment to 5
  const addItem2 = await post("/carts/items", { user_id: uid, product_id: pid2, quantity: 1 });
  let cart = (await get(`/carts?user_id=${uid}`)).body;
  const line1 = cart.items.find((i) => i.product_id === pid);
  ok("cart add + increment", line1 && line1.quantity === 5, `qty=${line1 && line1.quantity}`);
  ok("cart 2 lines + total>0", cart.items.length === 2 && cart.total > 0, `total=${cart.total}`);
  await put(`/carts/items/${addItem2.body.id}`, { quantity: 4 });
  cart = (await get(`/carts?user_id=${uid}`)).body;
  ok("cart update qty", cart.items.find((i) => i.product_id === pid2).quantity === 4);

  // create order from cart -> cart should clear
  const order = await post("/orders", { user_id: uid, payment_method: "COD", shipping_fee: 30000, from_cart: true });
  ok("create order from cart", order.status === 201 && order.body.order_number, `no=${order.body.order_number} final=${order.body.final_amount}`);
  cart = (await get(`/carts?user_id=${uid}`)).body;
  ok("cart cleared after order", cart.items.length === 0);
  const odet = (await get(`/orders/${order.body.id}`)).body;
  ok("order detail has items", odet.items && odet.items.length === 2, `items=${odet.items && odet.items.length}`);
  const ords = (await get(`/orders?user_id=${uid}`)).body;
  ok("order listed for user", ords.length === 1);

  // ---- precise cleanup (only this user's data) ----
  await connectDB();
  const mongoose = require("mongoose");
  const db = mongoose.connection.db;
  const { Types } = mongoose;
  const u = new Types.ObjectId(uid);
  const carts = await db.collection("carts").find({ user_id: u }).toArray();
  const cartIds = carts.map((c) => c._id);
  const orders = await db.collection("orders").find({ user_id: u }).toArray();
  const orderIds = orders.map((o) => o._id);
  const r = {
    users: (await db.collection("users").deleteOne({ _id: u })).deletedCount,
    membership: (await db.collection("membershiphistories").deleteMany({ user_id: u })).deletedCount,
    addresses: (await db.collection("useraddresses").deleteMany({ user_id: u })).deletedCount,
    wishlists: (await db.collection("wishlists").deleteMany({ user_id: u })).deletedCount,
    cartitems: (await db.collection("cartitems").deleteMany({ cart_id: { $in: cartIds } })).deletedCount,
    carts: (await db.collection("carts").deleteMany({ user_id: u })).deletedCount,
    orderitems: (await db.collection("orderitems").deleteMany({ order_id: { $in: orderIds } })).deletedCount,
    orderhist: (await db.collection("orderstatushistories").deleteMany({ order_id: { $in: orderIds } })).deletedCount,
    orders: (await db.collection("orders").deleteMany({ user_id: u })).deletedCount,
  };
  console.log("CLEANUP:", JSON.stringify(r));
  await mongoose.connection.close();
  process.exit(0);
})().catch((e) => { console.error("ERROR", e); process.exit(1); });
