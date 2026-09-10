async function checkStatus() {

    const message =
        document.getElementById("statusMessage");

    message.innerText = "Checking application...";

    try {

        const response =
            await fetch("/api/status");

        if (!response.ok) {
            throw new Error("Server error");
        }

        const result =
            await response.text();

        message.innerText =
            "✓ " + result;

    } catch (error) {

        message.innerText =
            "✗ Application is not responding.";

        console.error(error);
    }
}