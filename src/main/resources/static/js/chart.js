function drawChart(chartData) {
    // Переменные для размеров диаграммы
    const chartWidth = 600;
    const chartHeight = 150;
    const chartMargin = 20;
    const chartBottomMargin = 30;

    const fontSize = '0.8em';
    const leftColumnMargin = 15;
    const columnMargin = 2;
    const chartValuesY = 7;
    const chartLabelsY = 18;
    const colorColumn = 'purple'; // Одинаковый цвет для всех столбиков
    // const columnX = chartMargin + leftColumnMargin;
    const columnY = chartHeight - chartBottomMargin;
    const horizontalAxisY = columnY;
    var num = chartData.length;
    var width = (chartWidth - chartMargin * 2 - (2 * leftColumnMargin) + columnMargin) / num - columnMargin;
    var columnMaxValue = getMaxValue();

    // Функция для отображения диаграммы
    function draw() {
        var chartContainer = document.querySelector('.chart-container');

        // Очищаем контейнер
        chartContainer.innerHTML = '';

        // Создаем SVG-элемент
        var svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('class', 'chart-svg');
        svg.setAttribute('viewBox', '0 0 ' + chartWidth + ' ' + chartHeight);
        svg.setAttribute('edgeMode', 'none');

        // Создаем столбики диаграммы
        var bars = createBars();

        // Добавляем столбики в SVG-элемент
        svg.appendChild(bars);

        // Создаем оси и подписи
        var verticalAxis = createVerticalAxis();
        var horizontalAxis = createHorizontalAxis();
        var labels = createLabels();

        // Добавляем оси и подписи в SVG-элемент
        svg.appendChild(verticalAxis);
        svg.appendChild(horizontalAxis);
        svg.appendChild(labels);

        // Добавляем SVG-элемент в контейнер
        chartContainer.appendChild(svg);
    }

    // Функция для создания вертикальной оси
    function createVerticalAxis() {
        var line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', chartMargin);
        line.setAttribute('y1', chartMargin);
        line.setAttribute('x2', chartMargin);
        line.setAttribute('y2', horizontalAxisY);
        line.setAttribute('stroke', 'black');
        line.setAttribute('stroke-width', '1');
        return line;
    }

    // Функция для создания горизонтальной оси
    function createHorizontalAxis() {
        var line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', chartMargin);
        line.setAttribute('y1', horizontalAxisY);
        line.setAttribute('x2', chartWidth - chartMargin);
        line.setAttribute('y2', horizontalAxisY);
        line.setAttribute('stroke', 'black');
        line.setAttribute('stroke-width', '1');
        return line;
    }

    // Функция для создания подписей значений
    function createLabels() {
        var labels = document.createElementNS('http://www.w3.org/2000/svg', 'g');
        labels.setAttribute('class', 'chart-labels');

        var labelX = 0;
        var valueY = horizontalAxisY - chartValuesY;
        var labelY = horizontalAxisY + chartLabelsY;
        var labelAnchor = 'middle';

        chartData.forEach(function (data, index) {
            labelX = chartMargin + leftColumnMargin + ((width + columnMargin) * index) + ((width + columnMargin) / 2)
            // Подписи по горизонтальной оси
            var label = document.createElementNS('http://www.w3.org/2000/svg', 'text');
            label.setAttribute('x', labelX.toString());
            label.setAttribute('y', labelY.toString());
            label.setAttribute('font-size', fontSize);
            label.setAttribute('text-anchor', labelAnchor);
            label.textContent = data.category;
            labels.appendChild(label);

            // Подписи по вертикальной оси - внутри столбиков над горизонтальной осью
            label = document.createElementNS('http://www.w3.org/2000/svg', 'text');
            label.setAttribute('x', labelX.toString());
            label.setAttribute('y', valueY.toString());
            label.setAttribute('text-anchor', labelAnchor);
            label.setAttribute('font-size', fontSize);
            label.setAttribute('stroke', 'black');
            label.setAttribute('stroke-width', '0.5');
            label.setAttribute('fill', 'white');
            label.textContent = data.value;
            labels.appendChild(label);

            // Вращение label на 90 градусов против часовой стрелки относительно его центра
            // var labelBox = label.getBBox();
            // var labelCenterX = labelBox.x + labelBox.width / 2;
            // var labelCenterY = labelBox.y + labelBox.height / 2;
            // label.setAttribute('transform', 'rotate(-90 ' + labelCenterX + ' ' + labelCenterY + ')');
        });
        return labels;
    }

    // Функция для создания столбиков диаграммы
    function createBars() {
        // Столбики
        var height;

        var bars = document.createElementNS('http://www.w3.org/2000/svg', 'g');
        bars.setAttribute('class', 'chart-bars');
        chartData.forEach(function (data, index) {
            var columnX = chartMargin + leftColumnMargin + ((width + columnMargin) * index); // Положение столбика
            height = (data.value / columnMaxValue) * (columnY-chartMargin);
            var rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
            rect.setAttribute('x', columnX.toString());
            rect.setAttribute('y', (columnY - height).toString());
            rect.setAttribute('width', width.toString());
            rect.setAttribute('height', height.toString());
            rect.setAttribute('fill', colorColumn);
            bars.appendChild(rect);
        });

        return bars;
    }

    // Функция для получения максимального значения из данных
    function getMaxValue() {
        var max = 0;
        chartData.forEach(function (data) {
            if (data.value > max) {
                max = data.value;
            }
        });
        return max;
    }

    draw();
}


