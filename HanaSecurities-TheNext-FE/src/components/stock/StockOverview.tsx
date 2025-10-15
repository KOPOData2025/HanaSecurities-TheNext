import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { stockApi } from '../../services/stockApi';
import type { StockBasicInfoData } from '../../types/stock.types';
import './StockOverview.css';

interface OverviewData {
  시장: string;
  증권그룹: string;
  거래소구분: string;
  결산월일: string;
  상장주수: string;
  상장자본금액: string;
  자본금: string;
  액면가: string;
  발행가: string;
  코스피200종목여부: string;
  유가증권시장상장일자: string;
  주식종류: string;
  산업분류코드: string;
  NXT거래종목여부: string;
}

const StockOverview: React.FC = () => {
  const { code } = useParams<{ code: string }>();
  const [activeTab, setActiveTab] = useState('기업분석');
  const [basicInfo, setBasicInfo] = useState<StockBasicInfoData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBasicInfo = async () => {
      if (!code) return;

      try {
        setLoading(true);
        const response = await stockApi.getStockBasicInfo('300', code);
        if (response.success && response.data) {
          setBasicInfo(response.data);
        }
      } catch (error) {
        console.error('주식 기본 정보 조회 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchBasicInfo();
  }, [code]);

  
  const formatNumber = (value: string | undefined): string => {
    if (!value || value === '' || value === '0') return '-';
    const num = parseFloat(value);
    if (isNaN(num)) return '-';
    return num.toLocaleString();
  };

  
  const formatDate = (date: string | undefined): string => {
    if (!date || date.length !== 8) return '-';
    return `${date.substring(0, 4)}-${date.substring(4, 6)}-${date.substring(6, 8)}`;
  };

  
  const getMarketType = (): string => {
    if (!basicInfo?.mketIdCd) return '-';
    switch (basicInfo.mketIdCd) {
      case 'STK': return '유가증권시장';
      case 'KSQ': return '코스닥시장';
      case 'KNX': return '코넥스시장';
      default: return basicInfo.mketIdCd;
    }
  };

  
  const getSecurityGroup = (): string => {
    if (!basicInfo?.sctyGrpIdCd) return '-';
    const mapping: { [key: string]: string } = {
      'BC': '수익증권',
      'DR': '주식예탁증서',
      'EF': 'ETF',
      'EN': 'ETN',
      'EW': 'ELW',
      'FE': '해외ETF',
      'FO': '선물옵션',
      'FS': '외국주권',
      'FU': '선물',
      'FX': '플렉스 선물',
      'GD': '금현물',
      'IC': '투자계약증권',
      'IF': '사회간접자본투융자회사',
      'KN': '코넥스주권',
      'MF': '투자회사',
      'OP': '옵션',
      'RT': '부동산투자회사',
      'SC': '선박투자회사',
      'SR': '신주인수권증서',
      'ST': '주권',
      'SW': '신주인수권 증권',
      'TC': '신탁수익증권'
    };
    return mapping[basicInfo.sctyGrpIdCd] || basicInfo.sctyGrpIdCd;
  };

  
  const getExchangeType = (): string => {
    if (!basicInfo?.excgDvsnCd) return '-';
    switch (basicInfo.excgDvsnCd) {
      case '01': return '증권';
      case '02': return '선물';
      case '03': return '옵션';
      default: return basicInfo.excgDvsnCd;
    }
  };

  
  const getStockKind = (): string => {
    if (!basicInfo?.stckKindCd) return '-';
    const mapping: { [key: string]: string } = {
      '000': '해당사항없음',
      '101': '보통주',
      '201': '우선주',
      '202': '2우선주',
      '203': '3우선주',
      '204': '4우선주',
      '205': '5우선주',
      '206': '6우선주',
      '207': '7우선주',
      '208': '8우선주',
      '209': '9우선주',
      '210': '10우선주',
      '211': '11우선주',
      '212': '12우선주',
      '213': '13우선주',
      '214': '14우선주',
      '215': '15우선주',
      '216': '16우선주',
      '217': '17우선주',
      '218': '18우선주',
      '219': '19우선주',
      '220': '20우선주',
      '301': '후배주',
      '401': '혼합주'
    };
    return mapping[basicInfo.stckKindCd] || basicInfo.stckKindCd;
  };

  const overviewData: OverviewData = {
    시장: getMarketType(),
    증권그룹: getSecurityGroup(),
    거래소구분: getExchangeType(),
    결산월일: basicInfo?.setlMmdd ? `${basicInfo.setlMmdd.substring(0, 2)}월 ${basicInfo.setlMmdd.substring(2, 4)}일` : '-',
    상장주수: formatNumber(basicInfo?.lstgStqt),
    상장자본금액: formatNumber(basicInfo?.lstgCptlAmt),
    자본금: formatNumber(basicInfo?.cpta),
    액면가: formatNumber(basicInfo?.papr),
    발행가: formatNumber(basicInfo?.issuPric),
    코스피200종목여부: basicInfo?.kospi200ItemYn === 'Y' ? '해당' : '미해당',
    유가증권시장상장일자: formatDate(basicInfo?.sctsMketLstgDt),
    주식종류: getStockKind(),
    산업분류코드: basicInfo?.stdIdstClsfCdName || '-',
    NXT거래종목여부: basicInfo?.nxtTrStopYn === 'Y' ? '해당' : '미해당'
  };

  return (
    <div className="stock-overview">
      {/* Sub Tabs */}
      <div className="sub-tabs">
        <button
          className={`sub-tab ${activeTab === '기업분석' ? 'active' : ''}`}
          onClick={() => setActiveTab('기업분석')}
        >
          기업분석
        </button>
        <button
          className={`sub-tab ${activeTab === 'IR정보' ? 'active' : ''}`}
          onClick={() => setActiveTab('IR정보')}
        >
          IR정보
        </button>
        <span className="sub-tab-link">통합기준</span>
      </div>

      {/* Overview Table */}
      {activeTab === '기업분석' && (
        <div className="overview-table-container">
          <table className="overview-table">
            <tbody>
              <tr>
                <td className="label">시장</td>
                <td className="value">{overviewData.시장}</td>
              </tr>
              <tr>
                <td className="label">증권그룹</td>
                <td className="value">{overviewData.증권그룹}</td>
              </tr>
              <tr>
                <td className="label">거래소구분</td>
                <td className="value">{overviewData.거래소구분}</td>
              </tr>
              <tr>
                <td className="label">결산월일</td>
                <td className="value">{overviewData.결산월일}</td>
              </tr>
              <tr>
                <td className="label">상장주수</td>
                <td className="value">{overviewData.상장주수}</td>
              </tr>
              <tr>
                <td className="label">상장자본금액</td>
                <td className="value">{overviewData.상장자본금액}</td>
              </tr>
              <tr>
                <td className="label">자본금</td>
                <td className="value">{overviewData.자본금}</td>
              </tr>
              <tr>
                <td className="label">액면가</td>
                <td className="value">{overviewData.액면가}</td>
              </tr>
              <tr>
                <td className="label">발행가</td>
                <td className="value">{overviewData.발행가}</td>
              </tr>
              <tr>
                <td className="label">코스피200종목여부</td>
                <td className="value">{overviewData.코스피200종목여부}</td>
              </tr>
              <tr>
                <td className="label">유가증권시장상장일자</td>
                <td className="value">{overviewData.유가증권시장상장일자}</td>
              </tr>
              <tr>
                <td className="label">주식종류</td>
                <td className="value">{overviewData.주식종류}</td>
              </tr>
              <tr>
                <td className="label">산업분류코드</td>
                <td className="value">{overviewData.산업분류코드}</td>
              </tr>
              <tr>
                <td className="label">NXT거래종목여부</td>
                <td className="value">{overviewData.NXT거래종목여부}</td>
              </tr>
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'IR정보' && (
        <div className="ir-info-content">
          <div className="empty-content">
            <p>IR 정보가 준비중입니다.</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default StockOverview;