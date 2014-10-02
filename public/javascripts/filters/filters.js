angular.module('app').filter('lessDate', function () {
    return function (items, filterDate) {
        var filtered = [];
        var filterDate = (filterDate && !isNaN(Date.parse(filterDate))) ? Date.parse(filterDate) : 0;
        if (items) {
            for (var i = 0; i < items.length; i++) {
                var item = items[i].birthday;
                if (item.birthday == filterDate) {
                    filtered.push(item);
                }
            }
            return filtered;
        }
    }
});