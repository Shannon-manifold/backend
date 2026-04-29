require('dotenv').config();

const { createApp } = require('./app');

const PORT = process.env.PORT || 8080;
const app = createApp();

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}.`);
});
