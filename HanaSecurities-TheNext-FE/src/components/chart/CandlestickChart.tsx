import React, { useEffect, useRef, useState } from 'react';
import './CandlestickChart.css';

interface CandleData {
  date: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
  change?: number;
  changeRate?: number;
  changeSign?: string;
  ma5?: number;
  ma20?: number;
  ma60?: number;
  ma120?: number;
}

interface CandlestickChartProps {
  data: CandleData[];
  period: string;
  timeRange: string;
}

const CandlestickChart: React.FC<CandlestickChartProps> = ({ data, period, timeRange }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [tooltip, setTooltip] = useState<{ x: number; y: number; data: CandleData; canvasX: number; canvasY: number } | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    const container = containerRef.current;
    if (!canvas || !container) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    
    const rect = container.getBoundingClientRect();
    canvas.width = rect.width * window.devicePixelRatio;
    canvas.height = rect.height * window.devicePixelRatio;
    canvas.style.width = `${rect.width}px`;
    canvas.style.height = `${rect.height}px`;
    ctx.scale(window.devicePixelRatio, window.devicePixelRatio);

    
    ctx.clearRect(0, 0, rect.width, rect.height);

    if (data.length === 0) return;

    
    const padding = { top: 40, right: 70, bottom: period === '분' ? 30 : 10, left: 10 };

    
    ctx.fillStyle = '#f8f8f8';
    ctx.fillRect(rect.width - padding.right, 0, padding.right, rect.height);

    
    ctx.strokeStyle = '#e0e0e0';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(rect.width - padding.right, 0);
    ctx.lineTo(rect.width - padding.right, rect.height);
    ctx.stroke();
    const chartWidth = rect.width - padding.left - padding.right;
    const chartHeight = rect.height - padding.top - padding.bottom;

    
    const prices = data.flatMap(d => [d.high, d.low]);
    
    const margin = period === '분' ? 0.995 : 0.98;
    const topMargin = period === '분' ? 1.005 : 1.02;
    const minPrice = Math.min(...prices) * margin;
    const maxPrice = Math.max(...prices) * topMargin;
    const priceRange = maxPrice - minPrice;

    
    const candleWidth = Math.max(3, Math.min(12, (chartWidth / data.length) * 0.6));
    const candleSpacing = chartWidth / data.length;

    
    ctx.strokeStyle = '#f0f0f0';
    ctx.lineWidth = 1;

    const gridLines = 8; 
    for (let i = 0; i <= gridLines; i++) {
      const y = padding.top + (chartHeight / gridLines) * i;

      ctx.beginPath();
      ctx.moveTo(padding.left, y);
      ctx.lineTo(rect.width - padding.right, y);
      ctx.stroke();
    }

    
    data.forEach((candle, index) => {
      const x = padding.left + index * candleSpacing + candleSpacing / 2;
      const yHigh = padding.top + ((maxPrice - candle.high) / priceRange) * chartHeight;
      const yLow = padding.top + ((maxPrice - candle.low) / priceRange) * chartHeight;
      const yOpen = padding.top + ((maxPrice - candle.open) / priceRange) * chartHeight;
      const yClose = padding.top + ((maxPrice - candle.close) / priceRange) * chartHeight;

      const isUp = candle.close >= candle.open;
      const color = isUp ? '#D64442' : '#3B7DD8';

      
      ctx.strokeStyle = color;
      ctx.lineWidth = 1;
      ctx.beginPath();
      ctx.moveTo(x, yHigh);
      ctx.lineTo(x, yLow);
      ctx.stroke();

      
      const bodyTop = Math.min(yOpen, yClose);
      const bodyHeight = Math.abs(yClose - yOpen) || 1;

      ctx.fillStyle = isUp ? '#D64442' : '#3B7DD8';
      ctx.fillRect(x - candleWidth / 2, bodyTop, candleWidth, bodyHeight);
    });

    
    const drawMA = (values: number[], color: string, lineWidth: number = 1) => {
      ctx.strokeStyle = color;
      ctx.lineWidth = lineWidth;
      ctx.beginPath();

      let started = false;
      values.forEach((value, index) => {
        if (value) {
          const x = padding.left + index * candleSpacing + candleSpacing / 2;
          const y = padding.top + ((maxPrice - value) / priceRange) * chartHeight;

          if (!started) {
            ctx.moveTo(x, y);
            started = true;
          } else {
            ctx.lineTo(x, y);
          }
        }
      });
      ctx.stroke();
    };

    
    if (data[0].ma5) drawMA(data.map(d => d.ma5 || 0), '#FF9500', 1);
    if (data[0].ma20) drawMA(data.map(d => d.ma20 || 0), '#AF52DE', 1);
    if (data[0].ma60) drawMA(data.map(d => d.ma60 || 0), '#5AC8FA', 1);
    if (data[0].ma120) drawMA(data.map(d => d.ma120 || 0), '#4CD964', 1);

    
    const highestCandle = data.reduce((prev, curr) => curr.high > prev.high ? curr : prev);
    const lowestCandle = data.reduce((prev, curr) => curr.low < prev.low ? curr : prev);

    const highIndex = data.indexOf(highestCandle);
    const lowIndex = data.indexOf(lowestCandle);

    
    const xHigh = padding.left + highIndex * candleSpacing + candleSpacing / 2;
    const yHighMark = padding.top + ((maxPrice - highestCandle.high) / priceRange) * chartHeight;

    
    const highDate = highestCandle.date;
    const highMonth = highDate.slice(5, 7);
    const highDay = highDate.slice(8, 10);
    const highChangeRate = (highestCandle as any).changeRate || 0;
    const highChangeSign = highChangeRate >= 0 ? '+' : '';
    const highText = `${highestCandle.high.toLocaleString()}원(${highChangeSign}${highChangeRate.toFixed(1)}%, ${highMonth}.${highDay} 09:33)`;

    ctx.fillStyle = '#D64442';
    ctx.font = '12px sans-serif';

    
    const highTextWidth = ctx.measureText(highText).width;
    const highTextX = xHigh + 10;

    
    if (highTextX + highTextWidth > rect.width - padding.right) {
      
      ctx.textAlign = 'right';
      ctx.fillText(highText, xHigh - 10, yHighMark - 5);
    } else {
      
      ctx.textAlign = 'left';
      ctx.fillText(highText, highTextX, yHighMark - 5);
    }

    
    const xLow = padding.left + lowIndex * candleSpacing + candleSpacing / 2;
    const yLowMark = padding.top + ((maxPrice - lowestCandle.low) / priceRange) * chartHeight;

    
    const lowDate = lowestCandle.date;
    const lowMonth = lowDate.slice(5, 7);
    const lowDay = lowDate.slice(8, 10);
    const lowChangeRate = (lowestCandle as any).changeRate || 0;
    const lowChangeSign = lowChangeRate >= 0 ? '+' : '';
    const lowText = `${lowestCandle.low.toLocaleString()}원(${lowChangeSign}${lowChangeRate.toFixed(1)}%, ${lowMonth}.${lowDay} 09:33)`;

    ctx.fillStyle = '#3B7DD8';
    ctx.font = '12px sans-serif';

    
    const lowTextWidth = ctx.measureText(lowText).width;
    const lowTextX = xLow + 10;

    
    if (lowTextX + lowTextWidth > rect.width - padding.right) {
      
      ctx.textAlign = 'right';
      ctx.fillText(lowText, xLow - 10, yLowMark + 15);
    } else {
      
      ctx.textAlign = 'left';
      ctx.fillText(lowText, lowTextX, yLowMark + 15);
    }

    
    ctx.fillStyle = '#555';
    ctx.font = '11px sans-serif';
    ctx.textAlign = 'right';

    
    const priceStep = Math.round(priceRange / 8 / 500) * 500; 
    const basePrice = Math.round(minPrice / 500) * 500;

    for (let i = 0; i <= 8; i++) {
      const price = basePrice + priceStep * i;
      if (price <= maxPrice && price >= minPrice) {
        const y = padding.top + ((maxPrice - price) / priceRange) * chartHeight;
        ctx.fillText(price.toLocaleString(), rect.width - 10, y + 4);
      }
    }

    
    if (period === '분') {
      ctx.fillStyle = '#555';
      ctx.font = '10px sans-serif';
      ctx.textAlign = 'center';

      
      const labelCount = Math.min(8, Math.floor(data.length / 10));
      const labelInterval = Math.floor(data.length / labelCount);

      for (let i = 0; i < data.length; i += labelInterval) {
        const candle = data[i];
        const x = padding.left + i * candleSpacing + candleSpacing / 2;
        const y = rect.height - 5;

        
        let timeLabel = '';
        if (candle.date.includes(' ')) {
          const timePart = candle.date.split(' ')[1];
          const [hours, minutes] = timePart.split(':');
          timeLabel = `${hours}:${minutes}`;
        } else if (candle.date.includes('T')) {
          const timePart = candle.date.split('T')[1];
          const [hours, minutes] = timePart.split(':');
          timeLabel = `${hours}:${minutes}`;
        }

        if (timeLabel) {
          ctx.fillText(timeLabel, x, y);
        }
      }

      
      if (data.length > 0) {
        const lastCandle = data[data.length - 1];
        const x = padding.left + (data.length - 1) * candleSpacing + candleSpacing / 2;
        const y = rect.height - 5;

        let timeLabel = '';
        if (lastCandle.date.includes(' ')) {
          const timePart = lastCandle.date.split(' ')[1];
          const [hours, minutes] = timePart.split(':');
          timeLabel = `${hours}:${minutes}`;
        } else if (lastCandle.date.includes('T')) {
          const timePart = lastCandle.date.split('T')[1];
          const [hours, minutes] = timePart.split(':');
          timeLabel = `${hours}:${minutes}`;
        }

        if (timeLabel) {
          ctx.fillText(timeLabel, x, y);
        }
      }
    }

    
    if (tooltip) {
      const padding = { top: 40, right: 70, bottom: 10, left: 10 };
      const chartWidth = rect.width - padding.left - padding.right;
      const chartHeight = rect.height - padding.top - padding.bottom;
      const candleSpacing = chartWidth / data.length;

      
      const index = Math.floor((tooltip.canvasX - padding.left) / candleSpacing);
      if (index >= 0 && index < data.length) {
        const x = padding.left + index * candleSpacing + candleSpacing / 2;

        
        ctx.strokeStyle = '#666666';
        ctx.lineWidth = 1;
        ctx.setLineDash([5, 5]);

        ctx.beginPath();
        ctx.moveTo(x, padding.top);
        ctx.lineTo(x, rect.height - padding.bottom);
        ctx.stroke();

        
        ctx.beginPath();
        ctx.moveTo(padding.left, tooltip.canvasY);
        ctx.lineTo(rect.width - padding.right, tooltip.canvasY);
        ctx.stroke();

        ctx.setLineDash([]);
      }
    }

  }, [data, period, timeRange, tooltip]);

  
  const handleCanvasClick = (e: React.MouseEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current;
    const container = containerRef.current;
    if (!canvas || !container) return;

    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const padding = { top: 40, right: 70, bottom: 10, left: 10 };
    const chartWidth = rect.width - padding.left - padding.right;
    const chartHeight = rect.height - padding.top - padding.bottom;
    const candleSpacing = chartWidth / data.length;

    
    if (x >= padding.left && x <= rect.width - padding.right &&
        y >= padding.top && y <= rect.height - padding.bottom) {
      
      const index = Math.floor((x - padding.left) / candleSpacing);
      if (index >= 0 && index < data.length) {
        const clickedData = data[index];
        setTooltip({
          x: e.clientX,
          y: e.clientY,
          data: clickedData,
          canvasX: x,
          canvasY: y
        });
      }
    } else {
      
      setTooltip(null);
    }
  };

  
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setTooltip(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <div ref={containerRef} className="candlestick-chart">
      <canvas ref={canvasRef} onClick={handleCanvasClick} style={{ cursor: 'crosshair' }} />
      {tooltip && (
        <div
          className="chart-tooltip"
          style={{
            position: 'fixed',
            left: tooltip.x + 10,
            top: tooltip.y - 50,
            background: 'rgba(0, 0, 0, 0.9)',
            color: 'white',
            padding: '8px 12px',
            borderRadius: '4px',
            fontSize: '12px',
            zIndex: 1000,
            pointerEvents: 'none',
            whiteSpace: 'nowrap'
          }}
        >
          <div>날짜: {tooltip.data.date}</div>
          <div>시가: {tooltip.data.open.toLocaleString()}원</div>
          <div>고가: {tooltip.data.high.toLocaleString()}원</div>
          <div>저가: {tooltip.data.low.toLocaleString()}원</div>
          <div>종가: {tooltip.data.close.toLocaleString()}원</div>
          <div>거래량: {(tooltip.data.volume / 1000).toFixed(0)}K</div>
          {tooltip.data.changeRate !== undefined && (
            <div style={{ color: tooltip.data.changeRate >= 0 ? '#ff4444' : '#4444ff' }}>
              등락률: {tooltip.data.changeRate > 0 ? '+' : ''}{tooltip.data.changeRate?.toFixed(2)}%
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default CandlestickChart;