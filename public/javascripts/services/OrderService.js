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
        return $http.get('/orders/employment/json/get', {params: {'employmentOrderId': id}})
            .then(function (result) {
                return result.data;
            });
    }

    this.update = function (order) {
        return $http.put('/orders/employment/update', order)
            .success(function (result) {
                return result;
            })
    }

});

angular.module('app').service('TransferOrderService', function ($http) {

    this.save = function (order) {
        return $http.post('/orders/transfer/save', order)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/orders/transfer/json/list').then(function (result) {
            return result.data;
        });
    }

    this.get = function (id) {
        return $http.get('/orders/transfer/json/get', {params: {'employmentOrderId': id}})
            .then(function (result) {
                return result.data;
            });
    }

    this.update = function (order) {
        return $http.put('/orders/transfer/update', order)
            .success(function (result) {
                return result;
            })
    }

});

angular.module('app').service('DismissalOrderService', function ($http) {

    this.save = function (order) {
        return $http.post('/orders/dismissal/save', order)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/orders/dismissal/json/list').then(function (result) {
            return result.data;
        });
    }

    this.get = function (id) {
        return $http.get('/orders/dismissal/json/get', {params: {'dismissalOrderId': id}})
            .then(function (result) {
                return result.data;
            });
    }

    this.update = function (order) {
        return $http.put('/orders/dismissal/update', order)
            .success(function (result) {
                return result;
            })
    }

});

angular.module('app').service('LeavingReasonService', function ($http) {

    this.save = function (order) {
        return $http.post('/orders/leaving_reason/save', order)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/orders/leaving_reason/json/list').then(function (result) {
            return result.data;
        });
    }

    this.get = function (id) {
        return $http.get('/orders/leaving_reason/json/get', {params: {'leavingReasonId': id}})
            .then(function (result) {
                return result.data;
            });
    }

    this.update = function (order) {
        return $http.put('/orders/leaving_reason/update', order)
            .success(function (result) {
                return result;
            })
    }

});

