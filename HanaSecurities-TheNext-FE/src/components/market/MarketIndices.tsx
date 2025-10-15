import React, { useEffect, useState } from 'react';
import { AreaChart, Area, ResponsiveContainer, YAxis } from 'recharts';
import { indexApi } from '../../services/indexApi';
import type { IndexPriceResponse, IndexTimePriceResponse } from '../../types/index.types';
import MarketIndicesSkeleton from './MarketIndicesSkeleton';
import './MarketIndices.css';

export interface MarketIndexData {
  name: string;
  value: string;
  change: string;
  changePercent: string;
  isPositive: boolean;
  chartData?: { value: number }[];
}

interface MarketIndicesProps {
  indices?: MarketIndexData[];
}

const MarketIndices: React.FC<MarketIndicesProps> = ({ indices: propsIndices }) => {
  const [indices, setIndices] = useState<MarketIndexData[]>([]);
  const [chartData, setChartData] = useState<{ [key: string]: { value: number }[] }>({});
  const [loading, setLoading] = useState(true);

  
  const formatNumber = (value: string) => {
    const num = parseFloat(value);
    return num.toLocaleString('ko-KR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  
  const convertToMarketIndexData = (
    priceData: IndexPriceResponse,
    timePriceData?: IndexTimePriceResponse
  ): MarketIndexData => {
    
    const isPositive = priceData.changeSign === '1' || priceData.changeSign === '2';

    
    let chartData: { value: number }[] = [];
    if (timePriceData && timePriceData.timePrices) {
      chartData = timePriceData.timePrices
        .slice(-30) 
        .map(tp => ({ value: parseFloat(tp.price) }));
    }

    return {
      name: priceData.indexName,
      value: formatNumber(priceData.currentPrice),
      change: Math.abs(parseFloat(priceData.changePrice)).toString(),
      changePercent: `${isPositive ? '+' : '-'}${Math.abs(parseFloat(priceData.changeRate)).toFixed(2)}%`,
      isPositive,
      chartData
    };
  };

  
  const loadIndexData = async () => {
    try {
      setLoading(true);

      
      const [priceData, timePriceData] = await Promise.all([
        indexApi.getAllIndexPrices(),
        indexApi.getAllIndexTimePrices()
      ]);

      
      const kospiData = convertToMarketIndexData(priceData.kospi, timePriceData.kospi);
      const kosdaqData = convertToMarketIndexData(priceData.kosdaq, timePriceData.kosdaq);
      const kospi200Data = convertToMarketIndexData(priceData.kospi200, timePriceData.kospi200);

      setIndices([kospiData, kosdaqData, kospi200Data]);

      
      const newChartData: { [key: string]: { value: number }[] } = {};
      newChartData[kospiData.name] = kospiData.chartData || [];
      newChartData[kosdaqData.name] = kosdaqData.chartData || [];
      newChartData[kospi200Data.name] = kospi200Data.chartData || [];
      setChartData(newChartData);

    } catch (err) {
      console.error('Failed to load index data:', err);

      
      const defaultIndices: MarketIndexData[] = [
        {
          name: '코스피',
          value: '3,210.75',
          change: '5.63',
          changePercent: '+0.18%',
          isPositive: true
        },
        {
          name: '코스닥',
          value: '818.00',
          change: '6.60',
          changePercent: '+0.81%',
          isPositive: true
        },
        {
          name: '코스피200',
          value: '433.44',
          change: '0.58',
          changePercent: '+0.13%',
          isPositive: true
        }
      ];
      setIndices(defaultIndices);
    } finally {
      setLoading(false);
    }
  };

  
  useEffect(() => {
    if (propsIndices) {
      setIndices(propsIndices);
      
      const newChartData: { [key: string]: { value: number }[] } = {};
      propsIndices.forEach(index => {
        newChartData[index.name] = index.chartData || [];
      });
      setChartData(newChartData);
    } else {
      
      loadIndexData();
    }
  }, [propsIndices]);

  
  if (loading && indices.length === 0) {
    return <MarketIndicesSkeleton count={3} />;
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
                        <linearGradient id={`gradient-${index.name.replace(/[^a-zA-Z0-9]/g, '')}-${index.isPositive ? 'pos' : 'neg'}`} x1="0" y1="0" x2="0" y2="1">
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
                        fill={`url(#gradient-${index.name.replace(/[^a-zA-Z0-9]/g, '')}-${index.isPositive ? 'pos' : 'neg'})`}
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

export default MarketIndices;