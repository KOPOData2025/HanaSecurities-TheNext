import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, useNavigate } from "react-router-dom";
import { ArrowLeft } from 'lucide-react';
import { AuthProvider } from "./contexts/AuthContext";
import Header from "./components/common/Header";
import MarketIndices from "./components/market/MarketIndices";
import ForeignIndices from "./components/market/ForeignIndices";
import RealTimeRanking from "./components/ranking/RealTimeRanking";
import ETPRanking from "./components/ranking/ETPRanking";
import BondRanking from "./components/ranking/BondRanking";
import NewsSection from "./components/news/NewsSection";
import BottomNavigation from "./components/navigation/BottomNavigation";
import Footer from "./components/common/Footer";
import StockDetail from "./components/stock/StockDetail";
import AdBanner from "./components/banner/AdBanner";
import PaymentPage from "./components/payment/PaymentPage";
import WatchlistTab from "./components/watchlist/WatchlistTab";
import NewsDetail from "./components/news/NewsDetail";
import AssetTab from "./components/asset/AssetTab";
import AssetDetail from "./components/asset/AssetDetail";
import OrderPage from "./components/order/OrderPage";
import SearchResultPage from "./components/search/SearchResultPage";
import ShoppingHome from "./components/shopping/ShoppingHome";
import ProductDetail from "./components/shopping/ProductDetail";
import PageHeader from "./components/common/PageHeader";
import CheckoutPage from "./components/shopping/CheckoutPage";
import PaymentCompletePage from "./components/shopping/PaymentCompletePage";
import BNPLApplicationPage from "./components/bnpl/BNPLApplicationPage";
import BNPLTermsPage from "./components/bnpl/BNPLTermsPage";
import BNPLInfoPage from "./components/bnpl/BNPLInfoPage";
import BNPLReviewPage from "./components/bnpl/BNPLReviewPage";
import BNPLCompletePage from "./components/bnpl/BNPLCompletePage";
import BNPLUsagePage from "./components/bnpl/BNPLUsagePage";
import RegisterPage from "./components/auth/RegisterPage";
import LoginPage from "./components/auth/LoginPage";
import RegisterCompletePage from "./components/auth/RegisterCompletePage";
import LoginCompletePage from "./components/auth/LoginCompletePage";
import ForeignStockSearch from "./components/foreignStock/ForeignStockSearch";
import ForeignWatchlistTab from "./components/foreignStock/ForeignWatchlistTab";
import GoldDetail from "./components/gold/GoldDetail";
import GoldOrderPage from "./components/gold/GoldOrderPage";
import "./App.css";

function HomePage() {
  const [activeTab, setActiveTab] = useState(() => {
    const savedTab = localStorage.getItem('activeTab');
    return savedTab || "국내";
  });

  useEffect(() => {
    localStorage.setItem('activeTab', activeTab);
  }, [activeTab]);


  return (
    <>
      <Header activeTab={activeTab} setActiveTab={setActiveTab} />
      <main className="main-content">
        {activeTab === "국내" ? (
          <>
            <MarketIndices />
            <RealTimeRanking />
            <NewsSection query="코스피" />
          </>
        ) : activeTab === "해외" ? (
          <>
            <ForeignIndices />
            <RealTimeRanking
              title="해외 실시간 랭킹"
              subtitle="실시간 글로벌 시세 기준"
              showCountrySelector={true}
            />
            <NewsSection title="글로벌 뉴스" query="트럼프" />
          </>
        ) : activeTab === "채권/ETP" ? (
          <>
            <AdBanner />
            <BondRanking />
            <ETPRanking />
            <Footer />
          </>
        ) : (
          <>
            <MarketIndices />
            <RealTimeRanking />
            <NewsSection query="코스피" />
          </>
        )}
        {activeTab !== "채권/ETP" && <Footer />}
      </main>
      <BottomNavigation />
    </>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/stock/:code" element={<StockDetail />} />
            <Route path="/watchlist" element={<WatchlistTab />} />
            <Route path="/news/:id" element={<NewsDetail />} />
            <Route path="/payment" element={
              <>
                <PaymentPage onClose={() => window.history.back()} />
                <BottomNavigation />
              </>
            } />
            <Route path="/asset" element={<AssetTab />} />
            <Route path="/asset/:type" element={<AssetDetail />} />
            <Route path="/order" element={<OrderPage />} />
            <Route path="/search-result" element={<SearchResultPage />} />
            <Route path="/shopping" element={
              <ShoppingPage />
            } />
            <Route path="/shopping/product/:id" element={
              <ProductDetail />
            } />
            <Route path="/shopping/payment" element={
              <CheckoutPage />
            } />
            <Route path="/shopping/payment/complete" element={
              <PaymentCompletePage />
            } />
            <Route path="/bnpl-application" element={
              <BNPLApplicationPage />
            } />
            <Route path="/bnpl-terms" element={
              <BNPLTermsPage />
            } />
            <Route path="/bnpl-info" element={
              <BNPLInfoPage />
            } />
            <Route path="/bnpl-review" element={
              <BNPLReviewPage />
            } />
            <Route path="/bnpl-complete" element={
              <BNPLCompletePage />
            } />
            <Route path="/bnpl-usage" element={
              <BNPLUsagePage />
            } />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register-complete" element={<RegisterCompletePage />} />
            <Route path="/login-complete" element={<LoginCompletePage />} />
            <Route path="/foreign-search" element={<ForeignSearchPage />} />
            <Route path="/stock/:exchangeCode/:stockCode" element={<StockDetail />} />
            <Route path="/foreign-watchlist" element={<ForeignWatchlistTab />} />
            <Route path="/gold/:productCode" element={<GoldDetail />} />
            <Route path="/gold-order" element={<GoldOrderPage />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

function ShoppingPage() {
  const navigate = useNavigate();

  return (
    <>
      <PageHeader
        title="쇼핑"
        leftAction={
          <button className="back-btn" onClick={() => navigate(-1)}>
            <ArrowLeft size={24} />
          </button>
        }
      />
      <ShoppingHome />
      <BottomNavigation />
    </>
  );
}

function ForeignSearchPage() {
  const navigate = useNavigate();

  return (
    <ForeignStockSearch
      isOpen={true}
      onClose={() => navigate(-1)}
    />
  );
}

export default App;
