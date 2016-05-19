var lasttr = false
var chart;
var chartType = 'line';
var fill = false;

$.getJSON("/blackbox/api/v1/tests?size=50").done(function(data){renderTestList(data)})

function renderTestList(data){
	//console.log(data);
	$.each(data.content, function(i, item) {

		var tagLinks = [];
		var arr = item.tags.split(',');
		for (i in arr){
			tagLinks.push($('<a>').attr('href', '#tag:' + arr[i]).text('#' + arr[i]).css('padding-right', '6px').css('white-space', 'nowrap'));
		}
		
		function render() {
        	if (lasttr) {
        		lasttr.remove();
        	}
        		
        	lasttr = $('<tr>')
        		.append($('<td>')
        				.attr('colspan', '6')
        					.append($('<canvas>').attr('id', 'performance-results')));
        	console.log(lasttr.html());
        	lasttr.insertAfter($(this).closest('tr'))
        	
        	renderChart(item.id); 
	    }
		
        var $tr = $('<tr>').append(
        	$('<td>').text(item.id),
            $('<td>').text(item.name).attr('title', item.testClass + '.' + item.methodName + '()'),
            $('<td>').text(item.concurrency),
            $('<td>').text(item.repeats),
            $('<td>').text(item.warmupRuns),
            $('<td>').append(tagLinks),
            $('<td>').append(
            	$('<a>')
            		.append($('<img>').attr('src', 'img/chart.png')).click(render))
        ).appendTo('#tests-table-body');
     });
    };


function renderChart(testId) {
	
	$.getJSON("/blackbox/api/v1/results/?testId=" + testId + '&size=10').done(function(d){
	
	var hasPerformance = false;
	for (x in d.content) {
		if (d.content[x].testPerformance){
			hasPerformance = true;
			break;
		}
	}
	
	if (! hasPerformance){
		return;
	}
		
	var labels = [];
	for (x in d.content) {
		labels.push(new Date(d.content[x].timestamp).toISOString().substring(0,19));
	}
		
	var avg = {label: 'Average', 
			data: [], 
			fill:fill,
			borderColor: "rgba(57,106,177,1)",
	        backgroundColor: "rgba(57,106,177,1)",
	        pointColor: "rgba(151,187,205,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(151,187,205,0.8)"};
	
	var median = {label: 'Median', 
			data: [], 
			fill:fill,
			borderColor: "rgba(114,147,203,1)",
	        backgroundColor: "rgba(114,147,203,1)",
	        pointColor: "rgba(151,187,205,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(151,187,205,0.8)"};
	
	var min = {label: 'Min', 
			data: [],
			fill:fill,
			borderColor: "rgba(62,150,81,1)",
	        backgroundColor: "rgba(62,150,81,1)",
	        pointColor: "rgba(70,191,189,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(70,191,189,0.8)"};
	
	var max = {label: 'Max', 
			data: [], 
			fill:fill,
			borderColor: "rgba(204,37,41,1)",
			backgroundColor: "rgba(204,37,41,1)",
	        strokeColor: "rgba(247,70,74,1)",
	        pointColor: "rgba(247,70,74,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(247,70,74,0.8)"};
	var deviation = {label: 'Deviation', 
			data: [],
			fill:fill,
			borderColor: "rgba(75,192,192,1)",
			backgroundColor: "rgba(75,192,192,1)",
			strokeColor: "rgba(247,70,74,1)",
	        pointColor: "rgba(247,70,74,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(247,70,74,0.8)"
			};
	
	for (x in d.content) {
		if (d.content[x].testPerformance) {
			avg.data.push(d.content[x].testPerformance.average);
			median.data.push(d.content[x].testPerformance.median);
			min.data.push(d.content[x].testPerformance.min);
			max.data.push(d.content[x].testPerformance.max);
			deviation.data.push(d.content[x].testPerformance.standardDeviation);
		}
	}
	
	var testName = 'Test ' + d.content[0].test.name;
	//$("#performance-results-header").html(testName)
	
	if (chart){
		chart.destroy();
	}
  chart = new Chart($("#performance-results"), {
	type: chartType,
	data: {
		labels: labels,
		datasets: [min, median, avg,  max, deviation]
	},
	options: {scaleStartValue : 0 }
  });
	});
}