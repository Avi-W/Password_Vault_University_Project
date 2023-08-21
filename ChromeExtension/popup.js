
const socket = new WebSocket('ws://localhost:8081');

// Create a promise resolver map to handle different message types
const resolvers = {};

socket.onmessage = function(event) {
    const data = event.data;
    const type = data.charCodeAt(0);
    const success = data.charCodeAt(1);

    if (resolvers[type]) {
        if ((type === 1 || type === 2 || type === 3 || type === 4 || type === 5) && success === 1) {
            resolvers[type]({ success: true, data: data });
        } else {
            // Resolve the promise with an error object instead of rejecting
            resolvers[type]({ success: false, errorMessage: "Operation failed or received unexpected message type" });
        }
        delete resolvers[type];
    } else {
        console.warn("Received unhandled message type:", type);
    }
};


// Function to toggle views
function toggleLoginState(isLoggedIn) {
    document.getElementById('not-logged-in').style.display = isLoggedIn ? 'none' : 'block';
    document.getElementById('logged-in').style.display = isLoggedIn ? 'block' : 'none';
}

//Sending message to server and creates a promise to resolve when a message is received
function sendMessageToServer(messageString, type) {
    return new Promise((resolve) => {
        resolvers[type] = resolve;
        socket.send(messageString);
    });
}

// When the popup is opened, get the current tab's URL
chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
    var currentTab = tabs[0]; 
    var currentURL = currentTab.url; 
    
    currentURL = currentURL.split('.').slice(1,2);
    // Set the URL as the value for the website input
    document.getElementById('set-website').value = currentURL;
    document.getElementById('get-website').value = currentURL;
});

document.getElementById('register').addEventListener('click', async () => {
    const password = document.getElementById('register-password').value;
    const buffer = new Uint8Array(1 + password.length);
    buffer[0] = 1; 
    for (let i = 0; i < password.length; i++) {
        buffer[i + 1] = password.charCodeAt(i); // Convert each character to ASCII value
    }

    try {
        const response = await sendMessageToServer(buffer.buffer, 1);

        if (!response.success) {
            alert("Registration failed");
            console.error(response.errorMessage);
            return;
        }
        
        alert("Registered successfully! \nPlease login to continue.");

    } catch (error) {
        alert("Registration failed");
    }
    document.getElementById('register-password').value = '';
});

document.getElementById('login').addEventListener('click', async () => {
    const password = document.getElementById('login-password').value;
    const buffer = new Uint8Array(1 + password.length);
    buffer[0] = 2;
    for (let i = 0; i < password.length; i++) {
        buffer[i + 1] = password.charCodeAt(i); // Convert each character to ASCII value
    }

    try {
        const response = await sendMessageToServer(buffer.buffer, 2);

        if (!response.success) {
            alert("Login failed");
            console.error(response.errorMessage);
            return;
        }
        
        toggleLoginState(true);
        alert("Logged in successfully!");
    } catch (error) {
        alert("Login failed");
    }
    document.getElementById('login-password').value = '';
});

document.getElementById('set').addEventListener('click', async () => {
    const website = document.getElementById('set-website').value;
    const username = document.getElementById('set-username').value;
    const password = document.getElementById('set-password').value;

    const buffer = new Uint8Array(1 + 10 + 20 + 20);
    buffer[0] = 3;
    for (let i = 0; i < website.length && i < 10; i++) {
        buffer[i + 1] = website.charCodeAt(i); // Convert each character to ASCII value
    }
    buffer[website.length+1] = '\0';
    for (let i = 0; i < username.length && i < 20; i++) {
        buffer[i + 11] = username.charCodeAt(i); // Convert each character to ASCII value
    }
    buffer[11+username.length] = '\0' 
    for (let i = 0; i < password.length && i < 20; i++) {
        buffer[i + 31] = password.charCodeAt(i); // Convert each character to ASCII value
    }
    buffer[31+password.length] = '\0';

    try {
        const response = await sendMessageToServer(buffer.buffer, 3);

        if (!response.success) {
            alert("Setting password failed");
            console.error(response.errorMessage);
            return;
        }
    
        alert("Password set successfully!");
    } catch (error) {
        alert("Setting password failed");
    }

    document.getElementById('set-website').value = '';
    document.getElementById('set-username').value = '';
    document.getElementById('set-password').value = '';
});

//Function ro remove trailing nulls from inputString which is received from the server
function removeTrailingNulls(inputString) {
    const trimmedString = inputString.replace(/\0+$/, ''); // Remove trailing nulls
    return trimmedString;
}

document.getElementById('get').addEventListener('click', async () => {
    const website = document.getElementById('get-website').value;

    const buffer = new Uint8Array(1 + 10);
    buffer[0] = 4;
    for (let i = 0; i < website.length && i < 10; i++) {
        buffer[i + 1] = website.charCodeAt(i); // Convert each character to ASCII value
    }

    try {
        const responseData = await sendMessageToServer(buffer.buffer, 4);

        if (!responseData.success) {
            alert("Getting password failed");
            console.error(responseData.errorMessage);
            return;
        }

        const websiteResult = removeTrailingNulls(responseData.data.substring(2, 12));
        const usernameResult = removeTrailingNulls(responseData.data.substring(12, 32));
        const passwordResult = removeTrailingNulls(responseData.data.substring(32, 52));
        
        showCustomAlert(usernameResult, passwordResult);
        
    } catch (error) {
        alert("Getting password failed");
    }
});

document.getElementById('logout').addEventListener('click', async () => {
    const buffer = new Uint8Array(1);
    buffer[0] = 5;

    try {
        const response = await sendMessageToServer(buffer.buffer, 5);
        
        if (!response.success) {
            alert("Logout failed");
            console.error(response.errorMessage);
            return;
        }
        
        toggleLoginState(false);
        alert("Logged out successfully!");
    } catch (error) {
        alert("Logout failed");
    }
});

//Functions for custom popup ------------------------------
function showCustomAlert(username, password) {
    document.getElementById('alert-username').textContent = username;
    document.getElementById('alert-password').textContent = password;
    document.getElementById('custom-alert').style.display = 'block';
}

document.addEventListener("DOMContentLoaded", function() {
    var closeButton = document.getElementById('closeAlertButton');
    closeButton.addEventListener('click', closeAlert, false);
}, false);

function closeAlert() {
    document.getElementById('custom-alert').style.display = 'none';
}
//End functions for custom popup ------------------------------
