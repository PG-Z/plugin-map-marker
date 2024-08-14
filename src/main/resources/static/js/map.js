let MAP_MARKER;

function _createForOfIteratorHelper(o, allowArrayLike) {
    var it = typeof Symbol !== "undefined" && o[Symbol.iterator] || o["@@iterator"];
    if (!it) {
        if (Array.isArray(o) || (it = _unsupportedIterableToArray(o)) || allowArrayLike && o && typeof o.length === "number") {
            if (it) o = it;
            var i = 0;
            var F = function F() {
            };
            return {
                s: F, n: function n() {
                    if (i >= o.length) return {done: true};
                    return {done: false, value: o[i++]}
                }, e: function e(_e) {
                    throw _e;
                }, f: F
            }
        }
        throw new TypeError("Invalid attempt to iterate non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.");
    }
    var normalCompletion = true, didErr = false, err;
    return {
        s: function s() {
            it = it.call(o)
        }, n: function n() {
            var step = it.next();
            normalCompletion = step.done;
            return step
        }, e: function e(_e2) {
            didErr = true;
            err = _e2
        }, f: function f() {
            try {
                if (!normalCompletion && it.return != null) it.return()
            } finally {
                if (didErr) throw err;
            }
        }
    }
}

function _unsupportedIterableToArray(o, minLen) {
    if (!o) return;
    if (typeof o === "string") return _arrayLikeToArray(o, minLen);
    var n = Object.prototype.toString.call(o).slice(8, -1);
    if (n === "Object" && o.constructor) n = o.constructor.name;
    if (n === "Map" || n === "Set") return Array.from(o);
    if (n === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)) return _arrayLikeToArray(o, minLen)
}

function _arrayLikeToArray(arr, len) {
    if (len == null || len > arr.length) len = arr.length;
    for (var i = 0, arr2 = new Array(len); i < len; i++) {
        arr2[i] = arr[i]
    }
    return arr2
}

function _classCallCheck(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
        throw new TypeError("Cannot call a class as a function");
    }
}

function _defineProperties(target, props) {
    for (var i = 0; i < props.length; i++) {
        var descriptor = props[i];
        descriptor.enumerable = descriptor.enumerable || false;
        descriptor.configurable = true;
        if ("value" in descriptor) descriptor.writable = true;
        Object.defineProperty(target, descriptor.key, descriptor)
    }
}

function _createClass(Constructor, protoProps, staticProps) {
    if (protoProps) _defineProperties(Constructor.prototype, protoProps);
    if (staticProps) _defineProperties(Constructor, staticProps);
    Object.defineProperty(Constructor, "prototype", {writable: false});
    return Constructor
}

var MapboxGLButtonControl = function () {
    "use strict";

    function MapboxGLButtonControl(_ref) {
        var _ref$className = _ref.className, className = _ref$className === void 0 ? "" : _ref$className,
            _ref$title = _ref.title, title = _ref$title === void 0 ? "" : _ref$title;
        _classCallCheck(this, MapboxGLButtonControl);
        this._className = className;
        this._title = title
    }

    _createClass(MapboxGLButtonControl, [{
        key: "onAdd", value: function onAdd(map) {
            this._btn = document.createElement("button");
            this._btn.className = "mapboxgl-ctrl-icon " + this._className;
            this._btn.type = "button";
            this._btn.title = this._title;
            this._btn.onclick = function () {
                map.flyTo({center: MAP_MARKER.center, zoom: MAP_MARKER.zoom})
            };
            this._container = document.createElement("div");
            this._container.className = "mapboxgl-ctrl-group mapboxgl-ctrl";
            this._container.appendChild(this._btn);
            return this._container
        }
    }, {
        key: "onRemove", value: function onRemove() {
            this._container.parentNode.removeChild(this._container);
            this._map = undefined
        }
    }]);
    return MapboxGLButtonControl
}();
var ctrlPolygon = new MapboxGLButtonControl({className: "mapbox-gl-draw_polygon", title: "返回"});
var MYMAP = function () {
    "use strict";

    function MYMAP(config) {
        _classCallCheck(this, MYMAP);
        config = config || {};
        this.data = config.data;
        this.clusterData = [];
        this.markers = [];
        this._create()
    }

    _createClass(MYMAP, [{
        key: "_create", value: function _create() {
            var _this = this;
            mapboxgl.accessToken = MAP_MARKER.token;
            var map = new mapboxgl.Map({
                container: "map",
                style: "mapbox://styles/mapbox/light-v9",
                center: MAP_MARKER.center,
                cluster: true,
                minZoom: parseInt(MAP_MARKER.minZoom),
                maxZoom: parseInt(MAP_MARKER.maxZoom),
                zoom: parseInt(MAP_MARKER.zoom)
            });
            mapboxgl.setRTLTextPlugin(MAP_MARKER.launguagePACK);
            map.addControl(new MapboxLanguage({defaultLanguage: "zh"}));
            map.addControl(new mapboxgl.NavigationControl({showCompass: false}));
            map.addControl(ctrlPolygon, "top-right");
            map.on("load", function (e) {
                if (_this.data) {
                    _this.cluster.load(_this.data.features);
                    _this.clusterData = {
                        type: "FeatureCollection",
                        features: _this.cluster.getClusters([-180.0, -90.0, 180.0, 90.0], MAP_MARKER.zoom)
                    };
                    _this.updateMarkers();
                    return document.querySelector("#map").classList.add("is-loaded")
                }
            });
            map.on("loaded", function () {
            });
            map.on("zoom", function () {
                var zoom = Math.floor(_this.map.getZoom());
                _this.clusterData = {
                    type: "FeatureCollection",
                    features: _this.cluster.getClusters([-180.0, -90.0, 180.0, 90.0], zoom)
                };
                _this.updateMarkers()
            });
            this.cluster = new Supercluster({radius: 26, maxZoom: 24});
            this.map = map
        }
    }, {
        key: "updateMarkers", value: function updateMarkers() {
            this.markers.forEach(function (m) {
                return m.remove()
            });
            this.markers = [];
            var _iterator = _createForOfIteratorHelper(this.clusterData.features), _step;
            try {
                for (_iterator.s(); !(_step = _iterator.n()).done;) {
                    var data = _step.value;
                    if (data.properties.cluster) this.addClusterMarker(data); else this.addPhotoMarker(data)
                }
            } catch (err) {
                _iterator.e(err)
            } finally {
                _iterator.f()
            }
        }
    }, {
        key: "createMarker", value: function createMarker() {
            var element = document.createElement("div");
            return element
        }
    }, {
        key: "addPhotoMarker", value: function addPhotoMarker(feature) {
            var markerElement = this.createMarker();
            markerElement.className = "marker";
            markerElement.style.setProperty("--photo", "url(\"".concat(feature.properties.image, "\""));
            var html = '<h3 class="marker--name">' + feature.properties.title + "</h3>";
            if (feature.properties.permalink && feature.properties.permalink.length > 0) {
                for (var i = feature.properties.permalink.length - 1; i >= 0; i--) {
                    html += '<p class="marker--link"><a target="_blank" href="' + feature.properties.permalink[i] + '">' + feature.properties.description[i] + "</a></p>"
                }
            } else {
                markerElement.classList.add("no-post");
                html += "<p>该地点暂无游记。</p>"
            }
            this.addMarkerToMap(markerElement, feature.geometry.coordinates, html)
        }
    }, {
        key: "addClusterMarker", value: function addClusterMarker(cluster1) {
            var _this2 = this;
            var markerElement = this.createMarker();
            markerElement.className = "marker cluster";
            markerElement.addEventListener("click", function (e) {
                return _this2.clusterDidClick(e, cluster1)
            });
            markerElement.dataset.cardinality = Math.min(MAP_MARKER.limit, cluster1.properties.point_count);
            this.addClusterToMap(markerElement, cluster1.geometry.coordinates)
        }
    }, {
        key: "addClusterToMap", value: function addClusterToMap(marker, coordinates) {
            var m = new mapboxgl.Marker(marker).setLngLat(coordinates).addTo(this.map);
            this.markers.push(m);
            return m
        }
    }, {
        key: "addMarkerToMap", value: function addMarkerToMap(marker, coordinates, html) {
            var m = new mapboxgl.Marker(marker).setLngLat(coordinates).setPopup(new mapboxgl.Popup({offset: 25}).setHTML("" + html)).addTo(this.map);
            this.markers.push(m);
            return m
        }
    }, {
        key: "clusterDidClick", value: function clusterDidClick(event, cluster) {
            var data = {type: "FeatureCollection", features: this.cluster.getLeaves(cluster.properties.cluster_id)};
            this.map.fitBounds(geojsonExtent(data), {padding: this.map.getContainer().offsetHeight * 0.32})
        }
    }]);
    return MYMAP
}();

if (typeof jQuery === 'undefined') {
    console.debug('jQuery is not defined. Please ensure jQuery is loaded before map.js.');
} else {
    jQuery(document).ready(function () {
        let selector = document.querySelector("#map");
        if (selector == null) {
            return;
        }
        let configStr = document.getElementById('map-marker-config').innerText;
        MAP_MARKER = JSON.parse(configStr);
        fetch(MAP_MARKER.api).then(function (response) {
            return response.json().then(function (json) {
                new MYMAP({data: json})
            })
        })
    });
}
