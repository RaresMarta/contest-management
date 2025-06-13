import React from 'react';

const Header: React.FC = () => {
  return (
    <header>
      {/* I want something minimal and effective */}
      <div className="hero-body">
        <div className="container has-text-centered">
          <h1 className="title is-1 mt-5 mb-5">Contest Tracker</h1>
        </div>
      </div>
    </header>
  );
};

export default Header;