var websocket,
    local_connection,
    local_channel,
    media_constrains = {
        "audio":true,
        "video":true
    },
    rtc_configuration = {
        "iceServers": [
            {
                "urls":"stun:stun.l.google.com:19302"
            }
        ]
    };

function open_socket() {
    websocket = new SockJS("https://" + window.location.hostname + ":8443/service");
    websocket.onopen = function (event) {
         console.log(event);
    };
    websocket.onclose = function (event) {
         console.log(event);
    };
    websocket.onerror = function (event) {
         console.log(event);
    };
    websocket.onmessage = function (socket_message) {
        if (!local_connection) {
            rtc_connect(false);
        }
        var message = JSON.parse(socket_message.data);
        if (message.type && message.type == "offer") {
            local_connection.setRemoteDescription(new RTCSessionDescription(message))
                .then(function () {
                    return local_connection.createAnswer();
                })
                .then(function (created_answer) {
                    return local_connection.setLocalDescription(created_answer);
                })
                .then(function () {
                    var answer = local_connection.localDescription;
                    websocket.send(JSON.stringify(answer));
                })
                .catch(function (error) {
                    console.log(error);
                });
        } else if (message.type && message.type == "answer") {
            local_connection.setRemoteDescription(new RTCSessionDescription(message))
                .catch(function (error) {
                    console.log(error);
                });
        } else {
            local_connection.addIceCandidate(new RTCIceCandidate(message))
                .catch(function (error) {
                    console.log(error);
                });
        }
    };
}

function rtc_connect(isInitiator) {
    local_connection = new RTCPeerConnection();
    local_connection.onicecandidate = function (event) {
        // console.log("Onicecandidate event!");
        // console.log(event);
        // console.log(JSON.stringify(event.candidate));
        websocket.send(JSON.stringify(event.candidate));
    };
    local_connection.onnegotiationneeded = function (event) {
        // console.log("Onnegotiationneeded event!");
        local_connection.createOffer()
            .then(function (offer) {
                //   console.log("Offer created!");
                //   console.log(offer);
                return local_connection.setLocalDescription(offer);
            })
            .then(function () {
                websocket.send(JSON.stringify(local_connection.localDescription));
                //   console.log(JSON.stringify(local_connection.localDescription));
                //  console.log("Offer send!");
            })
            .catch(function (error) {
                // console.log("Offer creation and send error!");
                // console.log(error);
            });
    };
    local_connection.ontrack = function (event) {
        console.log("Ontrack event");
        var rmtrack = event.streams[0];
        console.log(rmtrack);
        var output = document.getElementById("remote_video");
        output.srcObject = rmtrack;
        console.log(output);
        console.log(output.srcObject);
    };
    if (isInitiator) {
        local_channel = local_connection.createDataChannel("channel");
        // console.log("Local channel created!");
        // console.log(local_channel);
        channel_events();
    } else {
        local_connection.ondatachannel = function (event) {
            //  console.log("Ondatachannel event!");
            // console.log(event);
            local_channel = event.channel;
            channel_events();
        };
    };
}
document.getElementById("rct_close").addEventListener(
    "click",
    function (event) {
        //  console.log("Close-socket button clicked!");
        // console.log(event.target);
        local_channel.close();
        local_connection.close();
        websocket.close(1000,"Close");
    }
);
document.getElementById("files").addEventListener(
    "change",
    function (event) {
        //  console.log("File selected and send!");
        //send file
    }
);
document.getElementById("local_message").addEventListener(
    "keydown",
    function (event) {
        if (event.key === "Enter") {
            document.getElementById("conversation").innerHTML += event.target.value + "\n";
            local_channel.send(event.target.value);
            event.target.value = "";
        }
    }
);
document.getElementById("rct_connect").addEventListener(
    "click",
    function () {
        // console.log("RCT Button clicked!");
        rtc_connect(true);
    }
);
document.getElementById("start_video").addEventListener(
    "click",
    function () {
        // console.log("Video chat button clicked!");
        start_video_chat();
    }
);
function channel_events() {
    local_channel.onopen = function (event) {
        // console.log("Local channel onopen event!");
        // console.log(event);
    };
    local_channel.onerror = function (event) {
        //  console.log("Local channel onerror event!");
        //  console.log(event);
    };
    local_channel.onmessage = function (message) {
        //  console.log("Local channel onmessage!");
        // console.log(message);
        document.getElementById("conversation").innerHTML += message.data + "\n";
    };
    var ch_status = window.setInterval(function () {
        //  console.log(local_channel.readyState);
    },2000);
}
function start_video_chat() {
    navigator.mediaDevices.getUserMedia(media_constrains)
        .then(function (stream) {
            document.getElementById("local_video").srcObject = stream;
            // var remote_view = new MediaStream();
            local_connection.addTrack(stream.getVideoTracks()[0],stream);
            console.log(local_connection);
            //[video_sender].forEach(function (sender) {
            //    remote_view.addTrack(local_connection.getReceivers.find(function (receiver) {
            //         return receiver.mid == sender.mid;
            //     }).track);
            // });
            // local_channel.send(out_going_stream);
        })
        .catch(function (error) {
            console.log("Get user media error");
            console.log(error);
        });
}
open_socket();