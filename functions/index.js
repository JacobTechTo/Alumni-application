/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const functions = require("firebase-functions");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

admin.initializeApp();

const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: "jacobofficial254@gmail.com", // Replace with your email
        pass: "supernatural254" // Replace with your email password
    }
});

// Send Email Notification
exports.sendEmailNotification = functions.firestore
    .document("notifications/{notificationId}")
    .onCreate((snap, context) => {
        const notification = snap.data();

        const mailOptions = {
            from: "your-email@gmail.com",
            to: notification.userEmail, // Alumni or Admin email
            subject: "Job Application Update",
            text: notification.message
        };

        return transporter.sendMail(mailOptions)
            .then(() => console.log("Email sent!"))
            .catch(error => console.error("Email failed: ", error));
    });

