// validation.js

function validateForm() {
    var titleFragment = document.getElementById("titleFragment").value.trim();
    var errorMessage = document.getElementById("errorMessage");

    if (titleFragment === '') {
        errorMessage.textContent = "Enter title";
        return false;
    }

    errorMessage.textContent = "";
    return true;
}