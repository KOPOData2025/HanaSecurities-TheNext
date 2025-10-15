import React, { useEffect, useRef } from 'react';
import './VolumeChart.css';

interface VolumeData {
  date: string;
  volume: number;
  priceChange: number; 
  color?: string; 
}

interface VolumeChartProps {
  data: VolumeData[];
  period: string;
  timeRange: string;
}

const VolumeChart: React.FC<VolumeChartProps> = ({ data, period, timeRange }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

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

    const padding = { top: 10, right: 70, bottom: 40, left: 10 };
    const chartWidth = rect.width - padding.left - padding.right;
    const chartHeight = rect.height - padding.top - padding.bottom;

    
    ctx.fillStyle = '#f8f8f8';
    ctx.fillRect(rect.width - padding.right, 0, padding.right, rect.height);

    
    ctx.strokeStyle = '#e0e0e0';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(rect.width - padding.right, 0);
    ctx.lineTo(rect.width - padding.right, rect.height - padding.bottom);
    ctx.stroke();

    
    ctx.fillStyle = '#f8f8f8';
    ctx.fillRect(0, rect.height - padding.bottom, rect.width, padding.bottom);

    
    ctx.strokeStyle = '#e0e0e0';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(0, rect.height - padding.bottom);
    ctx.lineTo(rect.width, rect.height - padding.bottom);
    ctx.stroke();

    
    const maxVolume = Math.max(...data.map(d => d.volume));
    const volumeScale = maxVolume * 1.1;

    
    const barWidth = Math.max(1, (chartWidth / data.length) * 0.7);
    const barSpacing = chartWidth / data.length;

    
    ctx.strokeStyle = '#f0f0f0';
    ctx.lineWidth = 1;
    const gridLines = 4;
    for (let i = 0; i <= gridLines; i++) {
      const y = padding.top + (chartHeight / gridLines) * i;

      ctx.beginPath();
      ctx.moveTo(padding.left, y);
      ctx.lineTo(rect.width - padding.right, y);
      ctx.stroke();
    }

    
    ctx.fillStyle = '#555';
    ctx.font = '11px sans-serif';
    ctx.textAlign = 'right';

    for (let i = 0; i <= gridLines; i++) {
      const y = padding.top + (chartHeight / gridLines) * i;
      const volume = (volumeScale / gridLines) * (gridLines - i);

      if (volume > 0) {
        const label = volume >= 1000000
          ? `${(volume / 1000000).toFixed(0)}M`
          : `${(volume / 1000).toFixed(0)}K`;

        ctx.fillText(label, rect.width - 5, y + 4);
      }
    }

    
    data.forEach((item, index) => {
      const x = padding.left + index * barSpacing + (barSpacing - barWidth) / 2;
      const barHeight = (item.volume / volumeScale) * chartHeight;
      const y = padding.top + chartHeight - barHeight;

      
      ctx.fillStyle = item.color || (item.priceChange >= 0 ? '#D64442' : '#3B7DD8');
      ctx.fillRect(x, y, barWidth, barHeight);
    });

    
    ctx.fillStyle = '#555';
    ctx.font = '11px sans-serif';
    ctx.textAlign = 'center';

    
    const labelCount = Math.min(6, data.length);
    const labelInterval = Math.floor(data.length / labelCount);

    for (let i = 0; i <= labelCount; i++) {
      const index = Math.min(i * labelInterval, data.length - 1);
      if (data[index]) {
        const x = padding.left + index * barSpacing + barSpacing / 2;
        const y = rect.height - padding.bottom + 15;

        let dateStr: string;
        if (period === '년') {
          
          dateStr = data[index].date.slice(0, 4);
        } else if (period === '월') {
          
          dateStr = data[index].date.slice(2, 4) + '.' + data[index].date.slice(5, 7);
        } else {
          
          dateStr = data[index].date.slice(5).replace('-', '.');
        }

        ctx.fillText(dateStr, x, y);
      }
    };

  }, [data, period, timeRange]);

  return (
    <div ref={containerRef} className="volume-chart">
      <canvas ref={canvasRef} />
    </div>
  );
};

export default VolumeChart;