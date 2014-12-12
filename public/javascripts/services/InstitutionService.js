angular.module('app').service('InstitutionService', function ($http) {

    this.save = function (institution) {
        return $http.post('/institutions/save', institution)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/institutions/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('QualificationService', function ($http) {

    this.save = function (qualification) {
        return $http.post('/qualification_types/save', qualification)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/qualification_types/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('EducationService', function ($http) {

    this.save = function (institution) {
        return $http.post('/educations/save', institution)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/educations/json/list').then(function (result) {
            return result.data;
        });
    }

    this.update = function (education) {
        return $http.put('/educations/update', education)
            .success(function (result) {
                return result;
            })
    }

    this.delete = function (education) {
        console.log(education);
        return $http.delete('/educations/delete', {params: {id: education.id}})
            .success(function (result) {
                return result;
            })
    }
});

angular.module('app').service('SeminarService', function ($http) {

    this.save = function (seminar) {
        return $http.post('/seminars/save', seminar)
            .success(function (result) {
                return result;
            });
    }

    this.getEmployeeSeminars = function (employeeId) {
        return $http.get('/seminars/json/list', {params: {employeeId: employeeId}}).then(function (result) {
            return result.data;
        });
    }

    this.update = function (seminar) {
        return $http.put('/seminars/update', seminar)
            .success(function (result) {
                return result;
            })
    }

    this.delete = function (seminar) {
        return $http.delete('/seminars/delete', {params: {id: seminar.id}})
            .success(function (result) {
                return result;
            })
    }
});