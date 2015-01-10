angular.module('app').service('CalendarTypeService', function ($http) {
    this.save = function (calendarType) {
        return $http.post('/calendar_types/save', calendarType)
            .success(function (result) {
                return result;
            });
    }
    this.list = function () {
        return $http.get('/calendar_types/json/list').then(function (result) {
            return result.data;
        });
    }
    this.getTypeYears = function (calendarTypeId) {
        return ($http.get('/calendar_types/json/years', {params: {calendarTypeId: calendarTypeId}}).then(handleSuccess, handleError));
    }

    // ---
    // PRIVATE METHODS.
    // ---


    // I transform the error response, unwrapping the application dta from
    // the API response payload.
    function handleError(response) {

        // The API response from the server should be returned in a
        // nomralized format. However, if the request was not handled by the
        // server (or what not handles properly - ex. server error), then we
        // may have to normalize it on our end, as best we can.
        if (
            !angular.isObject(response.data) || !response.data.message
            ) {

            return( $q.reject("An unknown error occurred.") );

        }

        // Otherwise, use expected error message.
        return( $q.reject(response.data.message) );

    }


    // I transform the successful response, unwrapping the application data
    // from the API response payload.
    function handleSuccess(response) {

        return( response.data );

    }
});

angular.module('app').service('DayTypeService', function ($http) {
    this.save = function (dayType) {
        return $http.post('/day_types/save', dayType)
            .success(function (result) {
                return result;
            });
    }
    this.list = function () {
        return $http.get('/day_types/json/list').then(function (result) {
            return result.data;
        });
    }
});

angular.module('app').service('CalendarService', function ($http, $q) {

    this.updateByTypeAndYear = function (calendarType, year, workingDayTypeId) {
        return ($http.get('/calendar/create', {params: {calendarTypeId: calendarType, year: year, workingDayTypeId: workingDayTypeId}}).then(handleSuccess, handleError));
    }

    this.getByTypeAndYear = function (calendarType, year) {
        return ($http.get('/calendar/json/list', {params: {calendarTypeId: calendarType, year: year}}).then(handleSuccess, handleError));
    }

    this.update = function (calendarDay) {
        return ($http.put('/calendar/update', calendarDay).then(handleSuccess, handleError));
    }


    // ---
    // PRIVATE METHODS.
    // ---


    // I transform the error response, unwrapping the application dta from
    // the API response payload.
    function handleError(response) {

        // The API response from the server should be returned in a
        // nomralized format. However, if the request was not handled by the
        // server (or what not handles properly - ex. server error), then we
        // may have to normalize it on our end, as best we can.
        if (
            !angular.isObject(response.data) || !response.data.message
            ) {

            return( $q.reject("An unknown error occurred.") );

        }

        // Otherwise, use expected error message.
        return( $q.reject(response.data.message) );

    }


    // I transform the successful response, unwrapping the application data
    // from the API response payload.
    function handleSuccess(response) {

        return( response.data );

    }

});




