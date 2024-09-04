$(document).ready(function() {
    $('#myButton').click(function() {
        alert('Button clicked!');
    });
});

$(document).ready(function() {

    var accessToken = localStorage.getItem('access_token');
    if (accessToken) {
        updateHeaderBarWithUser(accessToken);
    }


    // Обработчик для кнопки Sign In
    $('#signInButton').on('click', function() {

        var formData = {
            email: $('#signInEmail').val(),
            password: $('#signInPassword').val()
        };

        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/api/v1/auth/authenticate',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {

                var accessToken = response.access_token;
                localStorage.setItem('access_token', accessToken);
                updateHeaderBarWithUser(accessToken);
                $('#signInModal').modal('hide');
            },
            error: function(error) {
                $('#signInError').show();
            }
        });
    });

    // Обработчик для кнопки Sign Up
    $('#signUpButton').on('click', function() {
        var username = $('#signUpUsername').val();
        var password = $('#signUpPassword').val();
        var confirmPassword = $('#signUpConfirmPassword').val();

        if (password !== confirmPassword) {
            alert('Passwords do not match. Please try again.');
            return;
        }

        var formData = {
            username: username,
            email: $('#signUpEmail').val(),
            password: password
        };

        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/api/v1/auth/register',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                var accessToken = response.access_token;
                localStorage.setItem('access_token', accessToken);
                updateHeaderBarWithUser(accessToken);
                $('#signUpModal').modal('hide');
            },
            error: function(error) {
                $('#signUpError').show();
                console.error(error);
            }
        });
    });

    function updateHeaderBarWithUser(accessToken) {
        var userName = decodeToken(accessToken).sub;
        $('#signInBarButton, #signUpBarButton').hide();
        $('#usernameButton').text('Welcome, ' + userName);
        $('#usernameBarButton').show();
        $('#logoutBarButton').show();
    }

    function decodeToken(token) {
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    }

    // Обработчик для кнопки Logout
    $('#logoutButton').on('click', function() {
        localStorage.removeItem('access_token');
        updateHeaderBarAfterLogout();
    });

    function updateHeaderBarAfterLogout() {
        $('#signInError').hide();
        $('#signUpError').hide();
        $('#logoutBarButton').hide();
        $('#usernameBarButton').hide();
        $('#signInBarButton, #signUpBarButton').show();
    }

});