import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ComposedChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, Cell } from 'recharts';
import BottomNavigation from '../navigation/BottomNavigation';
import './ChartDetail.css';

interface ChartDataPoint {
  time: string;
  open: number;
  high: number;
  low: number;
  close: number;
  ma5?: number;
  ma20?: number;
  ma60?: number;
  ma120?: number;
}

interface VolumeDataPoint {
  time: string;
  volume: number;
  isPositive: boolean;
}

const ChartDetail: React.FC = () => {
  const { code } = useParams<{ code: string }>();
  const navigate = useNavigate();
  const [selectedPeriod, setSelectedPeriod] = useState('일');
  const [selectedMA, setSelectedMA] = useState<number[]>([5, 20, 60, 120]);

  const chartData: ChartDataPoint[] = [
    { time: '07.15', open: 6000, high: 6200, low: 5900, close: 6100, ma5: 6050, ma20: 6000, ma60: 5950, ma120: 5900 },
    { time: '07.16', open: 6100, high: 6300, low: 6050, close: 6250, ma5: 6100, ma20: 6020, ma60: 5960, ma120: 5910 },
    { time: '07.17', open: 6250, high: 6400, low: 6200, close: 6350, ma5: 6180, ma20: 6050, ma60: 5970, ma120: 5920 },
    { time: '07.18', open: 6350, high: 6500, low: 6300, close: 6450, ma5: 6250, ma20: 6100, ma60: 5980, ma120: 5930 },
    { time: '07.19', open: 6450, high: 6600, low: 6400, close: 6550, ma5: 6350, ma20: 6150, ma60: 5990, ma120: 5940 },
    { time: '07.22', open: 6550, high: 6700, low: 6500, close: 6650, ma5: 6450, ma20: 6200, ma60: 6000, ma120: 5950 },
    { time: '07.23', open: 6650, high: 6800, low: 6600, close: 6750, ma5: 6550, ma20: 6250, ma60: 6010, ma120: 5960 },
    { time: '07.24', open: 6750, high: 6900, low: 6700, close: 6850, ma5: 6650, ma20: 6300, ma60: 6020, ma120: 5970 },
    { time: '07.25', open: 6850, high: 7000, low: 6800, close: 6950, ma5: 6750, ma20: 6350, ma60: 6030, ma120: 5980 },
    { time: '07.26', open: 6950, high: 7100, low: 6900, close: 7050, ma5: 6850, ma20: 6400, ma60: 6040, ma120: 5990 },
    { time: '07.29', open: 7050, high: 7200, low: 7000, close: 7150, ma5: 6950, ma20: 6450, ma60: 6050, ma120: 6000 },
    { time: '07.30', open: 7150, high: 7300, low: 7100, close: 7250, ma5: 7050, ma20: 6500, ma60: 6060, ma120: 6010 },
    { time: '07.31', open: 7250, high: 7400, low: 7200, close: 7350, ma5: 7150, ma20: 6550, ma60: 6070, ma120: 6020 },
    { time: '08.01', open: 7350, high: 7500, low: 7300, close: 7450, ma5: 7250, ma20: 6600, ma60: 6080, ma120: 6030 },
    { time: '08.02', open: 7450, high: 7600, low: 7400, close: 7550, ma5: 7350, ma20: 6650, ma60: 6090, ma120: 6040 },
    { time: '08.05', open: 7550, high: 7700, low: 7500, close: 7650, ma5: 7450, ma20: 6700, ma60: 6100, ma120: 6050 },
    { time: '08.06', open: 7650, high: 7800, low: 7600, close: 7750, ma5: 7550, ma20: 6750, ma60: 6110, ma120: 6060 },
    { time: '08.07', open: 7750, high: 7900, low: 7700, close: 7850, ma5: 7650, ma20: 6800, ma60: 6120, ma120: 6070 },
    { time: '08.08', open: 7850, high: 8000, low: 7800, close: 7950, ma5: 7750, ma20: 6850, ma60: 6130, ma120: 6080 },
    { time: '08.09', open: 7950, high: 8100, low: 7900, close: 8050, ma5: 7850, ma20: 6900, ma60: 6140, ma120: 6090 },
    { time: '08.12', open: 8050, high: 8200, low: 8000, close: 8150, ma5: 7950, ma20: 6950, ma60: 6150, ma120: 6100 },
    { time: '08.13', open: 8150, high: 8300, low: 8100, close: 8250, ma5: 8050, ma20: 7000, ma60: 6160, ma120: 6110 },
    { time: '08.14', open: 8250, high: 8400, low: 8200, close: 8350, ma5: 8150, ma20: 7050, ma60: 6170, ma120: 6120 },
    { time: '08.16', open: 8350, high: 8500, low: 8300, close: 8450, ma5: 8250, ma20: 7100, ma60: 6180, ma120: 6130 },
    { time: '08.19', open: 8450, high: 8600, low: 8400, close: 8550, ma5: 8350, ma20: 7150, ma60: 6190, ma120: 6140 },
    { time: '08.20', open: 8550, high: 8700, low: 8500, close: 8650, ma5: 8450, ma20: 7200, ma60: 6200, ma120: 6150 },
    { time: '08.21', open: 8650, high: 9200, low: 8600, close: 9100, ma5: 8550, ma20: 7250, ma60: 6210, ma120: 6160 },
    { time: '08.22', open: 9100, high: 9500, low: 9000, close: 9400, ma5: 8750, ma20: 7350, ma60: 6220, ma120: 6170 },
    { time: '08.23', open: 9400, high: 9700, low: 9300, close: 9600, ma5: 8950, ma20: 7450, ma60: 6230, ma120: 6180 },
    { time: '08.26', open: 9600, high: 9900, low: 9500, close: 9800, ma5: 9250, ma20: 7550, ma60: 6240, ma120: 6190 },
    { time: '08.27', open: 9800, high: 10100, low: 9700, close: 10000, ma5: 9500, ma20: 7650, ma60: 6250, ma120: 6200 },
    { time: '08.28', open: 10000, high: 10300, low: 9900, close: 10200, ma5: 9700, ma20: 7750, ma60: 6260, ma120: 6210 },
    { time: '08.29', open: 10200, high: 10500, low: 10100, close: 10400, ma5: 9900, ma20: 7850, ma60: 6270, ma120: 6220 },
    { time: '08.30', open: 10400, high: 10700, low: 10300, close: 10600, ma5: 10100, ma20: 7950, ma60: 6280, ma120: 6230 },
    { time: '09.02', open: 10600, high: 10900, low: 10500, close: 10800, ma5: 10300, ma20: 8050, ma60: 6290, ma120: 6240 },
    { time: '09.03', open: 10800, high: 11100, low: 10700, close: 11000, ma5: 10500, ma20: 8150, ma60: 6300, ma120: 6250 },
    { time: '09.04', open: 11000, high: 11300, low: 10900, close: 11200, ma5: 10700, ma20: 8250, ma60: 6310, ma120: 6260 },
    { time: '09.05', open: 11200, high: 11500, low: 11100, close: 11400, ma5: 10900, ma20: 8350, ma60: 6320, ma120: 6270 },
    { time: '09.06', open: 11400, high: 11700, low: 11300, close: 11560, ma5: 11100, ma20: 8450, ma60: 6330, ma120: 6280 },
    { time: '09.09', open: 11560, high: 11560, low: 11300, close: 11400, ma5: 11300, ma20: 8550, ma60: 6340, ma120: 6290 }
  ];

  const volumeData: VolumeDataPoint[] = [
    { time: '07.15', volume: 5000, isPositive: true },
    { time: '07.16', volume: 7000, isPositive: false },
    { time: '07.17', volume: 4000, isPositive: true },
    { time: '07.18', volume: 8000, isPositive: true },
    { time: '07.19', volume: 3000, isPositive: false },
    { time: '07.22', volume: 6000, isPositive: true },
    { time: '07.23', volume: 4500, isPositive: false },
    { time: '07.24', volume: 5500, isPositive: true },
    { time: '07.25', volume: 3500, isPositive: false },
    { time: '07.26', volume: 7500, isPositive: true },
    { time: '07.29', volume: 4200, isPositive: false },
    { time: '07.30', volume: 8200, isPositive: true },
    { time: '07.31', volume: 3800, isPositive: false },
    { time: '08.01', volume: 20000, isPositive: true },
    { time: '08.02', volume: 4600, isPositive: false },
    { time: '08.05', volume: 5800, isPositive: true },
    { time: '08.06', volume: 3200, isPositive: false },
    { time: '08.07', volume: 6800, isPositive: true },
    { time: '08.08', volume: 4100, isPositive: false },
    { time: '08.09', volume: 7200, isPositive: true },
    { time: '08.12', volume: 3600, isPositive: false },
    { time: '08.13', volume: 8500, isPositive: true },
    { time: '08.14', volume: 4800, isPositive: false },
    { time: '08.16', volume: 5200, isPositive: true },
    { time: '08.19', volume: 3900, isPositive: false },
    { time: '08.20', volume: 7800, isPositive: true },
    { time: '08.21', volume: 18000, isPositive: true },
    { time: '08.22', volume: 22000, isPositive: true },
    { time: '08.23', volume: 40000, isPositive: true },
    { time: '08.26', volume: 35000, isPositive: true },
    { time: '08.27', volume: 28000, isPositive: true },
    { time: '08.28', volume: 15000, isPositive: false },
    { time: '08.29', volume: 12000, isPositive: false },
    { time: '08.30', volume: 11000, isPositive: false },
    { time: '09.02', volume: 10000, isPositive: false },
    { time: '09.03', volume: 9000, isPositive: false },
    { time: '09.04', volume: 8500, isPositive: false },
    { time: '09.05', volume: 8000, isPositive: false },
    { time: '09.06', volume: 7500, isPositive: false },
    { time: '09.09', volume: 29468, isPositive: true }
  ];

  const periods = ['일', '주', '월', '년', '분', '틱'];
  const maOptions = [5, 20, 60, 120];
  const tabs = ['요약', '호가', '차트', '커뮤니티', '체결', '거래'];

  const formatPrice = (price: number) => {
    return price.toLocaleString('ko-KR');
  };

  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload[0]) {
      return (
        <div className="custom-tooltip">
          <p className="tooltip-price">{`${formatPrice(payload[0].value)}원`}</p>
        </div>
      );
    }
    return null;
  };

  const CandleStick = (props: any) => {
    const { x, y, width, height, payload } = props;
    const isPositive = payload.close >= payload.open;
    const color = isPositive ? '#E60000' : '#0066FF';
    
    const chartHeight = 120;
    const yMin = 5000;
    const yMax = 12000;
    const yScale = chartHeight / (yMax - yMin);
    
    const getY = (value: number) => chartHeight - ((value - yMin) * yScale);
    
    const highY = getY(payload.high);
    const lowY = getY(payload.low);
    const openY = getY(payload.open);
    const closeY = getY(payload.close);
    
    const bodyTop = Math.min(openY, closeY);
    const bodyHeight = Math.abs(closeY - openY) || 1;
    
    return (
      <g>
        <line
          x1={x + width / 2}
          y1={highY}
          x2={x + width / 2}
          y2={lowY}
          stroke={color}
          strokeWidth={1}
        />
        <rect
          x={x + width * 0.3}
          y={bodyTop}
          width={width * 0.4}
          height={bodyHeight}
          fill={isPositive ? color : '#fff'}
          stroke={color}
          strokeWidth={1}
        />
      </g>
    );
  };

  const maColors = {
    5: '#FFA500',
    20: '#FF1493',
    60: '#0000FF',
    120: '#008000'
  };

  return (
    <div className="chart-detail-page">
      <div className="chart-detail-container">
        <div className="stock-header">
          <button className="back-button" onClick={() => navigate(-1)}>
            <span className="back-arrow">←</span>
          </button>
          <div className="stock-search">
            <span className="stock-search-text">원익홀딩스</span>
          </div>
          <div className="header-actions">
            <button className="search-icon">🔍</button>
            <button className="menu-dropdown">통합 <span className="dropdown-arrow">▼</span></button>
            <button className="order-button">주문</button>
            <button className="more-icon">⋮</button>
          </div>
        </div>

        <div className="stock-info">
          <div className="stock-basic">
            <span className="stock-code">030530</span>
            <span className="stock-market">코스닥</span>
            <span className="stock-type">KRX+NXT</span>
            <span className="stock-label">신고</span>
            <div className="stock-meta">
              <span className="meta-item">종 60</span>
              <span className="meta-item">신 60</span>
            </div>
          </div>
          
          <div className="stock-price-info">
            <div className="current-price">
              <span className="price">11,560</span>
              <span className="change-arrow">▲</span>
              <span className="change">2,220</span>
              <span className="change-percent">23.77%</span>
            </div>
            <div className="volume-info">
              29,468,384주
            </div>
          </div>
        </div>

        <div className="stock-tabs">
          {tabs.map((tab) => (
            <button 
              key={tab} 
              className={`stock-tab ${tab === '차트' ? 'active' : ''}`}
            >
              {tab}
            </button>
          ))}
          <button className="tab-arrow">〈</button>
        </div>

        <div className="chart-controls">
          <div className="chart-periods">
            {periods.map((period) => (
              <button
                key={period}
                className={`period-btn ${selectedPeriod === period ? 'active' : ''}`}
                onClick={() => setSelectedPeriod(period)}
              >
                {period}
              </button>
            ))}
          </div>
          <div className="chart-actions">
            <button className="expand-icon">⤢</button>
            <button className="download-icon">↓</button>
            <button className="settings-icon">⚙</button>
          </div>
        </div>

        <div className="ma-options">
          <span className="ma-label">가격(수정)</span>
          {maOptions.map((ma) => (
            <label
              key={ma}
              className={`ma-checkbox ${selectedMA.includes(ma) ? 'active' : ''}`}
            >
              <input
                type="checkbox"
                checked={selectedMA.includes(ma)}
                onChange={() => {
                  if (selectedMA.includes(ma)) {
                    setSelectedMA(selectedMA.filter(m => m !== ma));
                  } else {
                    setSelectedMA([...selectedMA, ma]);
                  }
                }}
              />
              <span 
                className="ma-dot"
                style={{ color: maColors[ma as keyof typeof maColors] }}
              >
                ●
              </span>
              <span className="ma-text">{ma}</span>
            </label>
          ))}
        </div>

        <div className="chart-container">
          <div className="chart-price-labels">
            <div className="high-price-label">
              <span className="price-value">11,760</span>
              <span className="price-date">(25.09.09)</span>
              <span className="price-percent">1.73%</span>
            </div>
            <div className="current-marker">
              <span className="marker-value">40</span>
            </div>
          </div>
          
          <ResponsiveContainer width="100%" height={120}>
            <ComposedChart 
              data={chartData} 
              margin={{ top: 5, right: 45, left: 5, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="1 1" stroke="#e0e0e0" />
              <XAxis 
                dataKey="time" 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 10, fill: '#999' }}
                interval={Math.floor(chartData.length / 6)}
              />
              <YAxis 
                domain={[5000, 12000]}
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 10, fill: '#999' }}
                tickFormatter={(value) => `${value / 1000}K`}
                orientation="right"
              />
              <Tooltip content={<CustomTooltip />} />
              
              <Bar 
                dataKey="close"
                shape={(props: any) => <CandleStick {...props} />}
              />
              
              {selectedMA.includes(5) && (
                <Line 
                  type="monotone" 
                  dataKey="ma5" 
                  stroke={maColors[5]} 
                  strokeWidth={1}
                  dot={false}
                />
              )}
              {selectedMA.includes(20) && (
                <Line 
                  type="monotone" 
                  dataKey="ma20" 
                  stroke={maColors[20]} 
                  strokeWidth={1}
                  dot={false}
                />
              )}
              {selectedMA.includes(60) && (
                <Line 
                  type="monotone" 
                  dataKey="ma60" 
                  stroke={maColors[60]} 
                  strokeWidth={1}
                  dot={false}
                />
              )}
              {selectedMA.includes(120) && (
                <Line 
                  type="monotone" 
                  dataKey="ma120" 
                  stroke={maColors[120]} 
                  strokeWidth={1}
                  dot={false}
                />
              )}
            </ComposedChart>
          </ResponsiveContainer>

          <div className="low-price-label">
            <span className="price-value">5,560</span>
            <span className="price-date">(25.08.04)</span>
            <span className="price-percent">-51.90%</span>
          </div>
        </div>

        <div className="volume-section">
          <div className="volume-header">
            <span className="volume-title">거래량</span>
            <div className="volume-controls">
              <button className="volume-toggle">☰</button>
              <button className="volume-close">✕</button>
            </div>
          </div>
          
          <div className="volume-current">
            <span className="volume-amount">40,000K</span>
            <span className="volume-value">29,468K</span>
          </div>

          <ResponsiveContainer width="100%" height={50}>
            <BarChart 
              data={volumeData} 
              margin={{ top: 5, right: 45, left: 5, bottom: 20 }}
            >
              <XAxis 
                dataKey="time" 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 9, fill: '#999' }}
                interval={Math.floor(volumeData.length / 6)}
              />
              <YAxis 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 9, fill: '#999' }}
                tickFormatter={(value) => `${value / 1000}K`}
                orientation="right"
              />
              <Bar dataKey="volume">
                {volumeData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.isPositive ? '#E60000' : '#0066FF'} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
      
      <BottomNavigation />
    </div>
  );
};

export default ChartDetail;