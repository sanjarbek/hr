angular.module('app').service('UserService', function ($http) {

    this.list = function () {
        return $http.get('/users/json/list').then(function (result) {
            return result.data;
        });
    }

//    this.update = function(user) {
//        return $http.put
//    }

    this.changePassword = function (userId, userNewPassword) {
        return $http.put('/users/json/change-password', { id: userId, newPassword: userNewPassword}).then(function (result) {
            return result.data;
        });
    }

});
