var jqxhr = $.getJSON("/blackbox/api/v1/results/?testId=1").done(function(data){renderChart(data)})

function renderChart(d) {
	console.log(d)
	
	var labels = [];
	for (x in d.content) {
		labels.push(new Date(d.content[x].timestamp).toISOString());
	}
	
	var fill = true;
	
	var avg = {label: 'Average', 
			data: [], 
			fill:fill,
			borderColor: "rgba(151,187,205,0.2)",
	        backgroundColor: "rgba(151,187,205,1)",
	        pointColor: "rgba(151,187,205,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(151,187,205,0.8)"};
	
	var min = {label: 'Min', 
			data: [],
			fill:fill,
			borderColor: "rgba(70,191,189,0.2)",
	        backgroundColor: "rgba(70,191,189,1)",
	        pointColor: "rgba(70,191,189,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(70,191,189,0.8)"};
	
	var max = {label: 'Max', 
			data: [], 
			fill:fill,
			borderColor: "rgba(247,70,74,0.2)",
			backgroundColor: "rgba(247,70,74,0.2)",
	        strokeColor: "rgba(247,70,74,1)",
	        pointColor: "rgba(247,70,74,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(247,70,74,0.8)"};
	var deviation = {label: 'Deviation', 
			data: [],
			fill:fill,
			borderColor: "rgba(75,192,192,0.4)",
			backgroundColor: "rgba(75,192,192,0.4)",
			strokeColor: "rgba(247,70,74,1)",
	        pointColor: "rgba(247,70,74,1)",
	        pointStrokeColor: "#fff",
	        pointHighlightFill: "#fff",
	        pointHighlightStroke: "rgba(247,70,74,0.8)"
			};
	
	for (x in d.content) {
		avg.data.push(d.content[x].testPerformance.average);
		min.data.push(d.content[x].testPerformance.min);
		max.data.push(d.content[x].testPerformance.max);
		deviation.data.push(d.content[x].testPerformance.standardDeviation);
	}
	
	var testName = 'Test ' + d.content[0].test.name;
	$("#performance-results-header").html(testName)
	
  var codesChart = new Chart($("#performance-results"), {
	type: 'bar',
	data: {
		labels: labels,
		datasets: [min, avg,  max, deviation]
	},
	options: {scaleStartValue : 0 }
  });
}