const functions = require('firebase-functions');
const nodemailer = require('nodemailer');
const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

const APP_NAME = 'Go/No-Go';
exports.sendBusinessVerificationEmail = functions.database.ref('/businesses/{pushId}').onCreate((snapshot, context) => {

  const email = gmailEmail; // The email of the user.
  const pushId = context.params.pushId; // The display name of the user.

  return sendBusinessVerificationEmail(email, pushId);
});


function sendBusinessVerificationEmail(email, pushId) {
  const mailOptions = {
    from: `${APP_NAME} <noreply@firebase.com>`,
    to: email,
  };

  mailOptions.subject = `${APP_NAME} new business`;
  mailOptions.text = `A new business was created with the push id: ${pushId}`;
  return mailTransport.sendMail(mailOptions).then(() => {
    return console.log('New welcome email sent to:', email);
  });
}
