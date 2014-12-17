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

    this.tagsList = function (query) {
        return $http.get('/orders/json/listByName', { params: { 'query': query }})
            .success(function (result) {
                return result;
            });
    }

});

angular.module('app').service('EmploymentOrderService', function ($http) {

    this.save = function (order) {
        return $http.post('/orders/employment/save', order)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/orders/employment/json/list').then(function (result) {
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