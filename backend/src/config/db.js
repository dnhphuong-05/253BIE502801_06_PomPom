const mongoose = require("mongoose");

async function connectDB() {
  const uri = process.env.MONGODB_URI;
  if (!uri) {
    throw new Error("MONGODB_URI is not set. Copy .env.example to .env and fill it in.");
  }
  // Our models use loose (empty) schemas, so query filters reference fields Mongoose
  // doesn't know about. strictQuery:false keeps those conditions instead of stripping them.
  mongoose.set("strictQuery", false);
  await mongoose.connect(uri, {
    serverSelectionTimeoutMS: 15000,
  });
  const { host, name } = mongoose.connection;
  console.log(`[db] Connected to MongoDB Atlas host=${host} db=${name}`);
  return mongoose.connection;
}

module.exports = { connectDB };
