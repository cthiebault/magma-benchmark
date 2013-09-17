$(function () {
  $('#container').highcharts({
    chart: {
      type: 'column'
    },
    title: {
      text: 'Writing values'
    },
    subtitle: {
      text: 'Importing data from Generated Datasource'
    },
    xAxis: {
      title: {
        text: 'Number of participants'
      },
      categories: ['100', '1000']
    },
    yAxis: {
      min: 0,
      title: {
        text: 'Time (ms)'
      }
    },
    tooltip: {
      formatter: function () {
        return this.series.name + ' wrote ' + this.x + ' values in<br/><b>' + this.key + '</b>';
      }
    },
    plotOptions: {
      column: {
        pointPadding: 0.2,
        borderWidth: 0
      }
    },
    series: [
      {
        name: 'JDBC Mysql',
        data: [
          {
            name: '4 seconds and 319 milliseconds',
            y: 4319
          },
          {
            name: '2 seconds and 875 milliseconds',
            y: 2875
          }
        ]
      },
      {
        name: 'JDBC Hsql',
        data: [
          {
            name: '769 milliseconds',
            y: 769
          },
          {
            name: '432 milliseconds',
            y: 432
          }
        ]
      },
//      {
//        name: 'Hibernate Mysql',
//        data: [4072, 21909]
//      },
//      {
//        name: 'Hibernate Hsql',
//        data: [93725, 2126589]
//      },
      {
        name: 'MongoDB',
        data: [
          {
            name: '484 milliseconds',
            y: 484
          },
          {
            name: '1 second and 830 milliseconds',
            y: 1830
          }
        ]
      }
    ]
  });
});