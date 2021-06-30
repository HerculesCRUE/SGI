const fs = require('fs');
const { DateTime } = require('luxon');
const handlebars = require('handlebars');

const inFile = 'src/assets/config/config.json.tmpl';
const outFile = 'src/assets/config/config.json';

try {
  var timeStamp = DateTime.now().setZone('Europe/Madrid').toFormat('yyyyLLddHHmmss');
  const source = fs.readFileSync(inFile, 'utf8');
  const template = handlebars.compile(source, {
    strict: true
  });

  const result = template({
    "buildTimeStamp": timeStamp
  });

  fs.writeFileSync(outFile, result);
  console.log('Build timestamp set to: ' + timeStamp);
} catch (error) {
  console.error('Error occurred:', error);
  throw error
}
