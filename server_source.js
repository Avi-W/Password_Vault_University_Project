// Import necessary modules
const http = require('http');
const websocket = require('websocket');
const net = require('net');

// Create a WebSocket server
const httpServer = http.createServer((req, res) => {});
httpServer.listen(8081, () => {
    console.log('WebSocket server listening on port 8081');
});
const wsServer = new websocket.server({
    httpServer: httpServer
});

// Create a TCP client to connect to the server
const client = new net.Socket();
client.connect(8080, '127.0.0.1', () => {
    console.log('Connected to TCP server');
});

// WebSocket server connection handler
wsServer.on('request', (request) => {
    const wsConnection = request.accept(null, request.origin);
    console.log('WebSocket client connected');

    // Handle WebSocket messages
    wsConnection.on('message', (message) => {
        if (message.type === 'binary') {
            console.log('Received data from WebSocket client, forwarding to TCP server');
            client.write(message.binaryData);
        }
    });

    // Handle TCP data and forward to WebSocket client
    client.on('data', (data) => {
        console.log('Received data from TCP server, forwarding to WebSocket client');
        string = new String(data);
        wsConnection.send(string);
    });
});

// Handle TCP connection close
client.on('close', () => {
    console.log('TCP connection closed');
});