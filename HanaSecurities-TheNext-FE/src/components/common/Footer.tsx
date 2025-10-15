import React from 'react';
import './Footer.css';

const Footer: React.FC = () => {
  return (
    <footer className="app-footer">
      <div className="footer-content">
        <div className="footer-section">
          <h4>시세정보 안내</h4>
          <p>
            증권 시세는 한국거래소, 코스콤, 한국예탁결제원, 한국신용평가,
            KIS채권평가, NICE P&I, FN자산평가에서 제공한 정보를 바탕으로 하며,
            오류가 있을 수 있고 지연될 수 있습니다.
          </p>
          <p>
            하나증권은 제공된 정보에 의한 투자결과에 법적책임을 지지 않습니다.
            게시된 정보는 무단으로 배포할 수 없습니다.
          </p>
        </div>
        
        <div className="footer-section">
          <h4>투자 유의사항</h4>
          <ul>
            <li>투자자는 금융투자상품에 대하여 하나증권으로부터 충분한 설명을 받을 권리가 있으며, 투자 전 상품설명서 및 약관을 반드시 읽어보시기 바랍니다.</li>
            <li>금융투자상품은 예금자보호법에 따라 예금보험공사가 보호하지 않습니다.</li>
            <li>금융투자상품은 자산가격 변동 등에 따라 투자원금의 손실(0~100%)이 발생할 수 있으며, 그 손실은 투자자에게 귀속됩니다.</li>
          </ul>
        </div>
        
        <div className="footer-info">
          <p className="company-info">
            하나증권 주식회사 | 대표이사 이상현 | 사업자등록번호 123-45-67890<br />
            서울특별시 영등포구 의사당대로 82 | 고객센터 1588-1588
          </p>
          <p className="copyright">
            Copyright © 2025 Hana Securities Co., Ltd. All Rights Reserved.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;