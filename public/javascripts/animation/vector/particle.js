(function() {

    var width, height, largeHeader, canvas, ctx, triangles, target, animateHeader = true;
    var colors = ['72,35,68', '43,81,102', '66,152,103', '250,178,67', '224,33,48'];

    // Main
    initHeader();
    addListeners();
    initAnimation();

    function initHeader() {
        width = window.innerWidth;
        height = window.innerHeight/2.3;
        if(height < 300)
        {
        	height=300;
        }
        target = {x: 0, y: height};

        largeHeader = document.getElementById('container_main');
        largeHeader.style.height = height+'px';

        canvas = document.getElementById('canvas_main');
        canvas.width = width;
        canvas.height = height;
        ctx = canvas.getContext('2d');

        // create particles
        triangles = [];
        for(var x = 0; x < 480; x++) {
            addTriangle(x*10);
        }
    }

    function addTriangle(delay) {
        setTimeout(function() {
            var t = new Triangle();
            triangles.push(t);
            tweenTriangle(t);
        }, delay);
    }

    function initAnimation() {
        animate();
    }

    function tweenTriangle(tri) {
        var t = Math.random()*(2*Math.PI);
        var x = (150+Math.random()*100)*Math.cos(t) + width*0.5;		// 파티클 도착지 x pos
        var y = (150+Math.random()*100)*Math.sin(t) + height*0.5-20;	// 파티클 도착지 y pos
        var time = 4+3*Math.random();

        TweenLite.to(tri.pos, time, {x: x,
            y: y, ease:Circ.easeOut,
            onComplete: function() {
                tri.init();
                tweenTriangle(tri);
        }});
    }

    // Event handling
    function addListeners() {
        window.addEventListener('scroll', scrollCheck);
        window.addEventListener('resize', resize);
    }

    function scrollCheck() {
        if(document.body.scrollTop > height) animateHeader = false;
        else animateHeader = true;
    }

    function resize() {
        width = window.innerWidth;
        height = window.innerHeight/2.3;
        if(height < 300)
        {
        	height=300;
        }
        largeHeader.style.height = height+'px';
        canvas.width = width;
        canvas.height = height;
    }

    function animate() {
        if(animateHeader) {
            ctx.clearRect(0,0,width,height);
            for(var i in triangles) {
                triangles[i].draw();
            }
        }
        requestAnimationFrame(animate);
    }

    // Canvas manipulation
    function Triangle() {
        var _this = this;

        // constructor
        (function() {
            _this.coords = [{},{},{}];
            _this.pos = {};
            init();
        })();

        function init() {								
            _this.pos.x = width*0.5;		// 파티클 출발지 x pos
            _this.pos.y = height*0.5-20;	// 파티클 출발지 y pos
            _this.coords[0].x = -10+Math.random()*30;
            _this.coords[0].y = -10+Math.random()*30;
            _this.coords[1].x = -10+Math.random()*30;
            _this.coords[1].y = -10+Math.random()*30;
            _this.coords[2].x = -10+Math.random()*30;
            _this.coords[2].y = -10+Math.random()*30;
            _this.scale = 0.1+Math.random()*0.9;
            _this.color = colors[Math.floor(Math.random()*colors.length)];
            setTimeout(function() { _this.alpha = 0.7; }, 100);		// 삼각형 파티클의 초기 투명도 지정
        }

        this.draw = function() {
            if(_this.alpha >= 0.005) _this.alpha -= 0.005;
            else _this.alpha = 0;
            ctx.beginPath();
            ctx.moveTo(_this.coords[0].x+_this.pos.x, _this.coords[0].y+_this.pos.y);
            ctx.lineTo(_this.coords[1].x+_this.pos.x, _this.coords[1].y+_this.pos.y);
            ctx.lineTo(_this.coords[2].x+_this.pos.x, _this.coords[2].y+_this.pos.y);
            ctx.closePath();
            ctx.fillStyle = 'rgba('+_this.color+','+ _this.alpha+')';
            ctx.fill();
        };

        this.init = init;
    }
    
})();