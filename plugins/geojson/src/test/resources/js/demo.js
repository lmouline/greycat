/**
 * Created by gnain on 12/09/16.
 */

var Demo = function () {

    var mymap;
    var userPositionMarker;
    var filterCircle;
    var markers;
    var graph;
    var currentContract;

    var init = function () {

        graph = new org.mwg.GraphBuilder().withStorage(new org.mwg.plugin.WSClient("ws://localhost:8050")).withPlugin(new org.mwg.structure.StructurePlugin()).build();
        graph.connect(function () {

            initOptionList();
            updateContract("Luxembourg");
            //console.log(graph);

        });
        initFlatpickr();
        initMap();
    };

    var initFlatpickr = function () {
        document.querySelector(".flatpickr").flatpickr();
    };

    var fillOptionListTask = org.mwg.task.Actions
        .hook(TimerHookFactory())
        .setTime("{{processTime}}")
        .fromIndexAll("cities")
        .get("name")
        .then(function (context) {
            var result = context.result();
            var s = result.size();

            var selector = document.querySelector("select")
            for (var i = 0; i < s; i++) {
                var val = result.get(i);
                var opt = document.createElement("option");
                opt.textContent = val;
                if (val == "Luxembourg") {
                    opt.setAttribute("selected", "");
                }
                selector.appendChild(opt);
            }
            context.continueTask();
        });

    var initOptionList = function () {
        console.log("Filling Option List");
        var context = fillOptionListTask.prepareWith(graph, null, function (result) {
            document.querySelector("#cities_init").textContent = document.querySelector("#cities_init").textContent + "... Done !";
        });
        context.setGlobalVariable("processTime", (new Date()).getTime());
        fillOptionListTask.executeUsing(context);
    };


    var initMap = function () {

        mymap = L.map('mapid').setView([49.632386, 6.168544], 10);
        L.tileLayer('https://api.mapbox.com/styles/v1/mapbox/streets-v9/tiles/256/{z}/{x}/{y}?access_token={accessToken}', {
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
            maxZoom: 18,
            id: 'test',
            accessToken: 'pk.eyJ1IjoiZ25haW4iLCJhIjoiY2lzbG96eWZwMDA3NzJucGtwMTd5bXh2MiJ9.tJUI9PFDrl7eENeVW9kaWw'
        }).on('load',function(e){
            document.querySelector("#map_init").textContent = document.querySelector("#map_init").textContent + "... Done !";
        }).addTo(mymap);

        mymap.on('click', function (event) {
            //updateNearest(e);
            userPositionMarker.setLatLng([event.latlng.lat, event.latlng.lng]);
            filterCircle.setLatLng([event.latlng.lat, event.latlng.lng]);
            updateFilter();
        });

        addUserPositionMarker();
        addFilterCircle();

    };

    var addUserPositionMarker = function () {
        userPositionMarker = L.marker([49.632386, 6.168544], {
            icon: L.icon({
                iconUrl: 'img/red_pin.png',
                iconAnchor: [15, 44],
                iconSize: [30, 44],
            }),
            draggable: true,
            zIndexOffset: 1000
        }).bindPopup("Your position").addTo(mymap);

        userPositionMarker["lastDrag"] = (new Date()).getTime();

        userPositionMarker.on("drag", function (event) {
            filterCircle.setLatLng([event.latlng.lat, event.latlng.lng]);
            if (((new Date()).getTime() - userPositionMarker.lastDrag) > 100) { // threshold
                userPositionMarker["lastDrag"] = (new Date()).getTime();
                updateFilter();
            }
        });

        userPositionMarker.on("dragend", function (event) {
            updateFilter();
        });

    };
    var addFilterCircle = function () {
        filterCircle = L.circle([49.632386, 6.168544], 2000).addTo(mymap);
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                userPositionMarker.setLatLng([position.coords.latitude, position.coords.longitude]);
            });
        } else {
            console.error("Geolocation is not supported by this browser.");
        }
    };


    var displayStationsTask = org.mwg.task.Actions
        .hook(TimerHookFactory())
        .setTime("{{processTime}}")
        .fromIndex("cities", "name={{contract_name}}")
        .traverseIndexAll("stations")
        .foreach(
            org.mwg.task.Actions.asVar("station").traverse("position").asVar("pos").fromVar("station")
                .then(function (context) {
                    addMarker(context.result().get(0));
                    context.continueTask();
                })
        ).then(function (context) {
            mymap.addLayer(markers);
            context.continueTask();
        });

    function updateContract(contract_name) {
        console.log("Updating contract:" + contract_name);
        currentContract = contract_name;
        if (markers != undefined) {
            mymap.removeLayer(markers);
        }
        markers = new L.FeatureGroup();

        var context = displayStationsTask.prepareWith(graph, null, function (result) {
            result.free();
            document.querySelector("#markers_loading").textContent = document.querySelector("#markers_loading").textContent + "... Done !";
            document.querySelector(".modalDialog").style.setProperty("opacity", 0);
        });
        context.setVariable("contract_name", contract_name);
        context.setVariable("processTime", (new Date()).getTime());

        displayStationsTask.executeUsing(context);
    }

    var askKDTree = org.mwg.task.Actions
    // .hook(hookFactory)
        .setTime("{{processTime}}")
        .fromIndex("cities", "name={{contract_name}}")
        .traverse("positions")
        //.print("{{result}}")
        .action(org.mwg.structure.action.NTreeNearestNWithinRadius.NAME, "{{actionParam}}")
        .then(function (context) {
            if (markers != undefined) {
                mymap.removeLayer(markers);
            }
            markers = new L.FeatureGroup();

            var nodes = context.resultAsNodes();
            for (var i = 0; i < nodes.size(); i++) {
                addMarker(nodes.get(i));
            }
            mymap.addLayer(markers);
            context.continueTask();
        });

    function updateFilter() {
        var form = document.querySelector("#filter_form");
        var nbStations = form.querySelector("[name=find_stations_nb]").value;
        var radiusStations = form.querySelector("[name=find_stations_radius]").value;
        filterCircle.setRadius(radiusStations);


        var context = askKDTree.prepareWith(graph, null, function (result) {
            result.free();
        });
        context.setVariable("actionParam", "" + userPositionMarker.getLatLng().lat + "," + userPositionMarker.getLatLng().lng + "," + nbStations + "," + radiusStations);
        context.setVariable("processTime", (new Date()).getTime());
        context.setVariable("contract_name", currentContract);
        askKDTree.executeUsing(context);
    };


    var addMarker = function (node) {

        node.rel("position", function (nodes) {
            var position = nodes[0];
            var marker = L.marker([position.get("lat"), position.get("lng")])
                .addTo(mymap)
                .bindPopup(node.get("name"));
            marker["node"] = node.id();
            marker.on('click', function (e) {
                graph.lookup(0, (new Date()).getTime(), e.target.node, function (node) {
                    document.querySelector("#all_stands").textContent = node.get("bike_stands");
                    document.querySelector("#free_stands").textContent = node.get("available_bike_stands");
                    document.querySelector("#available_bikes").textContent = node.get("available_bikes");
                });

            });
            markers.addLayer(marker);
        });
    };

    var getGraph = function() {
        return graph;
    };

    return {
        init: init,
        updateContract: updateContract,
        updateFilter: updateFilter,
        graph : getGraph
    };
};
