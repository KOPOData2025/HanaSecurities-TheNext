import React, { useEffect, useState } from 'react';
import { ResponsiveContainer, AreaChart, Area, YAxis } from 'recharts';
import { foreignIndexApi, type ForeignIndexResponse } from '../../services/foreignIndexApi';
import ForeignIndicesSkeleton from './ForeignIndicesSkeleton';
import './MarketIndices.css';

interface MarketIndexData {
  name: string;
  value: string;
  change: string;
  changePercent: string;
  isPositive: boolean;
  chartData?: { value: number }[];
}

const ForeignIndices: React.FC = () => {
  const [indices, setIndices] = useState<MarketIndexData[]>([]);
  const [chartData, setChartData] = useState<{ [key: string]: { value: number }[] }>({});
  const [loading, setLoading] = useState(true);

  const convertToMarketIndexData = (data: ForeignIndexResponse): MarketIndexData => {
    if (!data || !data.time_prices || data.time_prices.length === 0) {
      return {
        name: data?.index_name || 'Unknown',
        value: '0',
        change: '0',
        changePercent: '0%',
        isPositive: true,
        chartData: []
      };
    }

    const latestPrice = data.time_prices[data.time_prices.length - 1];
    const isPositive = latestPrice.change_sign === '2' || latestPrice.change_sign === '1';

    
    const chartData = data.time_prices
      .slice(-100)
      .map(item => ({
        value: parseFloat(item.price)
      }));

    
    const formatPrice = (price: string) => {
      const num = parseFloat(price);
      return num.toLocaleString('ko-KR', { maximumFractionDigits: 2 });
    };

    
    const formatChange = (change: string) => {
      const num = Math.abs(parseFloat(change));
      return num.toLocaleString('ko-KR', { maximumFractionDigits: 2 });
    };

    return {
      name: data.index_name,
      value: formatPrice(latestPrice.price),
      change: formatChange(latestPrice.change_price),
      changePercent: `${isPositive ? '+' : '-'}${Math.abs(parseFloat(latestPrice.change_rate))}%`,
      isPositive,
      chartData
    };
  };

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const allIndices = await foreignIndexApi.getAllIndices();

        
        const spxData = convertToMarketIndexData(allIndices.SPX);
        const shangData = convertToMarketIndexData(allIndices.SHANG);
        const sx5eData = convertToMarketIndexData(allIndices.SX5E);
        const hsceData = convertToMarketIndexData(allIndices.HSCE);

        setIndices([spxData, shangData, sx5eData, hsceData]);

        
        const newChartData: { [key: string]: { value: number }[] } = {};
        newChartData[spxData.name] = spxData.chartData || [];
        newChartData[shangData.name] = shangData.chartData || [];
        newChartData[sx5eData.name] = sx5eData.chartData || [];
        newChartData[hsceData.name] = hsceData.chartData || [];
        setChartData(newChartData);

      } catch (err) {
        console.error('Failed to load foreign index data:', err);

        
        const defaultIndices: MarketIndexData[] = [
          {
            name: 'S&P500',
            value: '6,643.70',
            change: '38.98',
            changePercent: '+0.59%',
            isPositive: true
          },
          {
            name: '상해종합',
            value: '3,250.80',
            change: '15.20',
            changePercent: '+0.47%',
            isPositive: true
          },
          {
            name: '유로 스톡스50',
            value: '20,728.31',
            change: '125.43',
            changePercent: '-0.60%',
            isPositive: false
          },
          {
            name: '홍콩H지수',
            value: '39,803.21',
            change: '298.54',
            changePercent: '+0.76%',
            isPositive: true
          }
        ];
        setIndices(defaultIndices);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
    
    const interval = setInterval(fetchData, 5 * 60 * 1000);

    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return <ForeignIndicesSkeleton />;
  }

  return (
    <div className="domestic-index-container">
      <div className="domestic-index-indices">
        {indices.map((index) => (
          <div key={index.name} className={`domestic-index-card ${index.isPositive ? 'positive' : 'negative'}`}>
            <div className="domestic-index-gradient-overlay"></div>
            <div className="domestic-index-content">
              <div className="domestic-index-title">{index.name}</div>
              <div className="domestic-index-value">{index.value}</div>
              <div className="domestic-index-change">
                <span className="domestic-index-arrow">{index.isPositive ? '▲' : '▼'}</span>
                <span className="domestic-index-change-value">{index.change}</span>
                <span className="domestic-index-change-percent">{index.changePercent}</span>
              </div>
            </div>
            <div className="domestic-index-chart">
              {chartData[index.name] && chartData[index.name].length > 0 && (() => {
                const data = chartData[index.name];
                const values = data.map(d => d.value);
                const minValue = Math.min(...values);
                const maxValue = Math.max(...values);
                const padding = (maxValue - minValue) * 0.1 || 1;

                return (
                  <ResponsiveContainer width="100%" height={55}>
                    <AreaChart data={data} margin={{ top: 5, right: 20, bottom: 10, left: 20 }}>
                      <defs>
                        <linearGradient id={`gradient-foreign-${index.name.replace(/[^a-zA-Z0-9]/g, '')}-${index.isPositive ? 'pos' : 'neg'}`} x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={index.isPositive ? "#FF6B6B" : "#4DABF7"} stopOpacity={0.4} />
                          <stop offset="50%" stopColor={index.isPositive ? "#FF6B6B" : "#4DABF7"} stopOpacity={0.2} />
                          <stop offset="100%" stopColor={index.isPositive ? "#FF6B6B" : "#4DABF7"} stopOpacity={0.08} />
                        </linearGradient>
                      </defs>
                      <YAxis
                        domain={[minValue - padding, maxValue + padding]}
                        hide={true}
                      />
                      <Area
                        type="basis"
                        dataKey="value"
                        stroke={index.isPositive ? "#FF8A95" : "#74B9FF"}
                        strokeWidth={1.8}
                        fill={`url(#gradient-foreign-${index.name.replace(/[^a-zA-Z0-9]/g, '')}-${index.isPositive ? 'pos' : 'neg'})`}
                        animationDuration={300}
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                );
              })()}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ForeignIndices;