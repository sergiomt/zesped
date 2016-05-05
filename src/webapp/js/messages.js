function Messages() {
    this.format = formatMessage;
}

function formatMessage(message) {
    var i=1;
    while(i<arguments.length) {
        message=message.replace("{"+(i-1)+"}",arguments[i++]);
    }
    return message;
}