angular.module('app').service('OrderService', function ($http) {

    this.save = function (order) {
        return $http.post('/orders/save', order)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/orders/json/list').then(function (result) {
            return result.data;
        });
    }

    this.get = function (id) {
        return $http.get('/orders/json/get', {params: {'id': id}})
            .then(function (result) {
                return result.data;
            });
    }

    this.update = function (order) {
        return $http.put('/orders/update', order)
            .success(function (result) {
                return result;
            })
    }

});