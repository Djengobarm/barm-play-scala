$(document).ready(function() {
    $.get("/users", function(users) {
        $("#users_container").empty();
        $.each(users, function(i, user) {
            $("#users_container").append("<li>" + user.name + "</li>")
        })
    });
});