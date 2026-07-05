const { Types } = require("mongoose");

function isObjectId(v) {
  return v instanceof Types.ObjectId;
}

// Convert a lean Mongo document to app-friendly JSON:
//  - `_id` becomes a string `id`
//  - any ObjectId field (foreign keys) becomes its hex string
function serialize(doc) {
  if (doc == null) return doc;
  if (Array.isArray(doc)) return doc.map(serialize);
  if (isObjectId(doc)) return doc.toString();
  if (doc instanceof Date) return doc.toISOString();
  if (typeof doc !== "object") return doc;

  const out = {};
  for (const [k, v] of Object.entries(doc)) {
    if (k === "_id") out.id = v == null ? null : v.toString();
    else if (k === "__v") continue;
    else out[k] = serialize(v);
  }
  return out;
}

module.exports = { serialize };
