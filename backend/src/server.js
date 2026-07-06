require("dotenv").config();
const express = require("express");
const cors = require("cors");
const mongoose = require("mongoose");
const { connectDB } = require("./config/db");

const app = express();
app.use(cors());
app.use(express.json({ limit: "5mb" }));

// Routes
const productsRouter = require("./routes/products");
app.use("/api/auth", require("./routes/auth"));
app.use("/api/users", require("./routes/users"));
app.use("/api/products", productsRouter);
app.use("/api/categories", productsRouter.categoriesRouter);
app.use("/api/orders", require("./routes/orders"));
app.use("/api/carts", require("./routes/carts"));
app.use("/api/community", require("./routes/community"));
app.use("/api/blogs", require("./routes/blogs"));
app.use("/api", require("./routes/experts"));
app.use("/api/nearby-posts", require("./routes/nearbyPosts"));
app.use("/api", require("./routes/catalog"));

// Health check: reports whether the API is up and connected to Atlas.
app.get("/api/health", (req, res) => {
  const states = ["disconnected", "connected", "connecting", "disconnecting"];
  res.json({
    ok: true,
    service: "pompom-backend",
    db: states[mongoose.connection.readyState] || "unknown",
    dbName: mongoose.connection.name || null,
    time: new Date().toISOString(),
  });
});

const PORT = process.env.PORT || 3000;

connectDB()
  .then(() => {
    app.listen(PORT, () => console.log(`[api] listening on http://localhost:${PORT}`));
  })
  .catch((err) => {
    console.error("[db] Connection failed:", err.message);
    // Still start the server so /api/health can report the disconnected state.
    app.listen(PORT, () => console.log(`[api] listening (DB down) on http://localhost:${PORT}`));
  });
