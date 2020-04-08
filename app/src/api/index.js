module.exports = require("./impl");

if (process.env.gem_API === "mock") {
  module.exports = require("./mock");
}
