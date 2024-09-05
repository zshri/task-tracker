$(document).ready(function() {
    // Глобальные переменные
    var accessToken = localStorage.getItem('access_token');

    // Инициализация
    if (accessToken) {
        updatePageAuth(accessToken);
    }

    // Глобальная переменная для хранения задач
    let tasks = [];

    // Функции для работы с API
    function getTasks(accessToken) {
        fetch('http://localhost:8080/api/v1/tasks', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        })
        .then(response => response.json())
        .then(fetchedTasks => {
            tasks = fetchedTasks; // Сохраняем задачи в глобальную переменную
            renderTasks(tasks);
        })
        .catch(error => console.error('Ошибка при получении задач:', error));
    }

    function createTask(title, accessToken) {
        const taskRequest = {
            title: title,
            description: "",
            status: "PENDING"
        };

        fetch('http://localhost:8080/api/v1/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(taskRequest)
        })
        .then(response => response.json())
        .then(data => {
            $('#newTaskTitle').val('');
            getTasks(accessToken);
        })
        .catch(error => console.error('Ошибка при создании задачи:', error));
    }

    function updateTask(taskId, title, description, status) {
        const accessToken = localStorage.getItem('access_token');
        const taskRequest = { title, description, status };

        fetch(`http://localhost:8080/api/v1/tasks/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(taskRequest)
        })
        .then(response => response.json())
        .then(updatedTask => {
            $('#taskEditModal').modal('hide');
            getTasks(accessToken);
        })
        .catch(error => console.error('Ошибка при обновлении задачи:', error));
    }

    function deleteTask(taskId) {
        const accessToken = localStorage.getItem('access_token');

        fetch(`http://localhost:8080/api/v1/tasks/${taskId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        })
        .then(response => {
            if (response.ok) {
                $('#taskEditModal').modal('hide');
                getTasks(accessToken);
            } else {
                throw new Error('Ошибка при удалении задачи');
            }
        })
        .catch(error => console.error('Ошибка при удалении задачи:', error));
    }

    // Функции для работы с UI
    function renderTasks(tasks) {
        $('#todoList').empty();
        $('#completedList').empty();
        tasks.forEach(task => {
            const taskItem = `
                <li class="list-group-item d-flex justify-content-between align-items-center" data-task-id="${task.id}" data-description="${task.description}">
                    <span class="task-title text-truncate">${task.title}</span>
                    <button class="edit-task btn btn-link p-0 ml-2" title="Edit">
                        <i class="fas fa-edit text-dark"></i>
                    </button>
                </li>`;
            if (task.status === 'COMPLETED') {
                $('#completedList').append(taskItem);
            } else {
                $('#todoList').append(taskItem);
            }
        });
    }

    function updatePageAuth(accessToken) {
        var userName = decodeToken(accessToken).sub;
        $('#signInBarButton, #signUpBarButton').hide();
        $('#usernameButton').text(userName);
        $('#usernameBarButton').show();
        $('#logoutBarButton').show();
        $('#contentMainWelcome').hide();
        $('#contentMainWithUser').show();
        
        getTasks(accessToken);
    }

    function updatePageNonAuth() {
        $('#signInError').hide();
        $('#signUpError').hide();
        $('#logoutBarButton').hide();
        $('#usernameBarButton').hide();
        $('#signInBarButton, #signUpBarButton').show();
        $('#contentMainWithUser').hide();
        $('#contentMainWelcome').show();
    }

    function decodeToken(token) {
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    }

    // Обработчики событий
    $('#addTaskButton').click(function() {
        const title = $('#newTaskTitle').val();
        if (title) {
            createTask(title, accessToken);
        }
    });

    $(document).on('click', '.edit-task', function() {
        const taskItem = $(this).closest('li');
        const taskId = taskItem.data('task-id');
        const task = tasks.find(t => t.id === taskId);

        if (task) {
            $('#taskTitle').val(task.title);
            $('#taskDescription').val(task.description);
            $('#taskCompleted').prop('checked', task.status === 'COMPLETED');
            $('#taskEditModal').data('task-id', taskId).modal('show');
        }
    });

    $('#saveTaskChanges').click(function() {
        const taskId = $('#taskEditModal').data('task-id');
        const title = $('#taskTitle').val();
        const description = $('#taskDescription').val();
        const status = $('#taskCompleted').is(':checked') ? 'COMPLETED' : 'PENDING';
        
        updateTask(taskId, title, description, status);
    });

    $('#deleteTaskButton').click(function() {
        const taskId = $('#taskEditModal').data('task-id');
        deleteTask(taskId);
    });

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
                accessToken = response.access_token;
                localStorage.setItem('access_token', accessToken);
                updatePageAuth(accessToken);
                $('#signInModal').modal('hide');
            },
            error: function(error) {
                $('#signInError').show();
            }
        });
    });

    $('#signUpButton').on('click', function() {
        var username = $('#signUpUsername').val();
        var password = $('#signUpPassword').val();
        var confirmPassword = $('#signUpConfirmPassword').val();

        if (password !== confirmPassword) {
            alert('Пароли не совпадают. Пожалуйста, попробуйте снова.');
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
                accessToken = response.access_token;
                localStorage.setItem('access_token', accessToken);
                updatePageAuth(accessToken);
                $('#signUpModal').modal('hide');
            },
            error: function(error) {
                $('#signUpError').show();
                console.error(error);
            }
        });
    });

    $('#logoutButton').on('click', function() {
        localStorage.removeItem('access_token');
        accessToken = null;
        updatePageNonAuth();
    });
});