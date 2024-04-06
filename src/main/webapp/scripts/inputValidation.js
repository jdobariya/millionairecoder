document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('playerRegistrationForm');
    const nameInput = document.getElementById('playerName');

    if (form != null) {
        form.addEventListener('submit', function (event) {
            event.preventDefault(); // Prevent the default form submission

            if (!validateName(nameInput.value)) {
                alert('Please enter a valid name (letters only, maximum length 50 characters).');
                return;
            }
            nameInput.value = nameInput.value.trim(); // Remove leading and trailing whitespace
            this.submit();
        });
    }

    function validateName(name) {
        const lettersOnlyRegex = /^[a-zA-Z\s]+$/; // Regular expression to match letters and spaces
        const maxLength = 50; // Maximum length for the name

        if (name.trim() === '') {
            return false; // Name is empty
        }

        if (name.length > maxLength) {
            return false; // Name exceeds maximum length
        }

        return lettersOnlyRegex.test(name);

         // Validation passed
    }
});
