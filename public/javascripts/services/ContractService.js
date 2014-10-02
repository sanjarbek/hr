angular.module('app').service('ContractTypeService', function ($http) {

    this.save = function (contract_type) {
        $http.post('/contract_types/save', contract_type)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/contract_types/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('ContractService', function ($http) {

    this.save = function (contract) {
        $http.post('/contracts/save', contract)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/contracts/json/list').then(function (result) {
            return result.data;
        });
    }

    this.employee_contracts = function (employee_id) {
        return $http.get('/contracts/json/ownlist', {params: {id: employee_id}}).then(function (result) {
            return result.data;
        });
    }

});
