$(function () {

  var jobs = ['import', 'vectorRead', 'delete'];

  var categories = [];
  var seriesByJob = {};
  $.each(data, function (key, val) {
    if (val === undefined) return;

    var jobName = $.trim(val.datasource + ' ' + val.flavor);

    if ($.inArray(val.nbEntities, categories) === -1) {
      categories.push(val.nbEntities);
    }

    $.each(jobs, function (key, job) {

      var jobSeries = seriesByJob[job];
      if (jobSeries === undefined) {
        seriesByJob[job] = {};
      }

      var series = seriesByJob[job][jobName];
      if (series === undefined) {
        series = [];
      }
      series.push({
        name: val[job + 'DurationFormatted'],
        y: val[job + 'Duration']
      });
      seriesByJob[job][jobName] = series;

    });

//    var importsByJob = seriesByJob['import'];
//    if (importsByJob === undefined) seriesByJob['import'] = {};
//
//    var importSeries = seriesByJob['import'][jobName];
//    if (importSeries === undefined) {
//      importSeries = [];
//    }
//    importSeries.push({
//      name: val.importDurationFormatted,
//      y: val.importDuration
//    });
//    seriesByJob['import'][jobName] = importSeries;
  });

  categories.sort();

  var importSeries = [];
  $.each(seriesByJob['import'], function (key, val) {
    importSeries.push({
      name: key,
      data: val
    });
  });

  var vectorReadSeries = [];
  $.each(seriesByJob['vectorRead'], function (key, val) {
    vectorReadSeries.push({
      name: key,
      data: val
    });
  });
  var deleteSeries = [];
  $.each(seriesByJob['delete'], function (key, val) {
    deleteSeries.push({
      name: key,
      data: val
    });
  });


  function drawChart(renderTo, title, subtitle, formatter, series) {
    var options = {
      chart: {
        renderTo: renderTo,
        type: 'column'
      },
      title: {
        text: title
      },
      subtitle: {
        text: subtitle
      },
      xAxis: {
        title: {
          text: 'Number of participants'
        },
        categories: categories
      },
      yAxis: {
        title: {
          text: 'Time (ms)'
        }
      },
      tooltip: {
        formatter: formatter
      },
      plotOptions: {
        column: {
          pointPadding: 0.2,
          borderWidth: 0
        }
      },
      series: series
    };
    new Highcharts.Chart(options);
  }

  drawChart('importChart', 'Writing values', 'Importing data from Generated Datasource',
    function () {
      return this.series.name + ' wrote ' + this.x + ' values in<br/><b>' + this.key + '</b>';
    },
    importSeries);

  drawChart('readVectorChart', 'Read vector', null,
    function () {
      return this.series.name + ' read ' + this.x + ' values vector in<br/><b>' + this.key + '</b>';
    },
    vectorReadSeries);

  drawChart('deleteChart', 'Delete Datasource', null,
    function () {
      return this.series.name + ' deletes datasource in<br/><b>' + this.key + '</b>';
    },
    deleteSeries);


});
