require("dotenv").config();
const mongoose = require("mongoose");
const { User } = require("./src/models");
const { toUserDto } = require("./src/dto");
(async () => {
  await mongoose.connect(process.env.MONGODB_URI);
  const u = await User.findOne({ full_name: "Nguyễn Thảo Nguyên" });
  console.log(JSON.stringify(await toUserDto(u), null, 2));
  await mongoose.disconnect();
})().catch(e => { console.error("ERR", e.message); process.exit(1); });
