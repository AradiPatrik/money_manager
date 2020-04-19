const functions = require('firebase-functions');

const admin = require('firebase-admin')

admin.initializeApp()

/**
* Initiate a recursive delete of documents at a given path.
*
* This delete is NOT an atomic operation and it's possible
* that it may fail after only deleting some documents.
*
* @param {string} data.path the document or collection path to delete.
*/
exports.deleteUser = () => functions.https.onCall((data, context) => {
    if (context.auth.uid !== data.userId)
        throw new functions.https.HttpsError(
            'permission-denied', 'Must be an administrative user to initiate delete.');
    const path = data.path;
    console.log(`User ${context.auth.uid} has requested to delete path ${path}`);

    return functions.firestore.delete(path, {
        project: process.env.GCLOUD_PROJECT,
        recursive: true,
        yes: true,
        token: functions.config().fb.token
    }).then(() => { return { path: path }; });
});

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

exports.helloWorld = functions.https.onRequest((request, response) => {
    response.send("Hello from Firebase!");
});
