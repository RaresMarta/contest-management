import React from "react";

interface FilterBarProps {
  filters: { type: string; age: string };
  setFilters: React.Dispatch<React.SetStateAction<{ type: string; age: string }>>;
}

/**
 * Renders two dropdowns ("Type" and "Age") to filter competitions.  
 * Calls setFilters whenever a selection changes.
 */
const FilterBar: React.FC<FilterBarProps> = ({ filters, setFilters }) => {
  const { type, age } = filters;

  const handleTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setFilters(prev => ({ ...prev, type: e.target.value }));
  };

  const handleAgeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setFilters(prev => ({ ...prev, age: e.target.value }));
  };

  return (
    <div className="field is-grouped is-align-items-center">
      {/* Type Dropdown */}
      <div className="control">
        <label className="label mb-1">Type:</label>
        <div className="select">
          <select value={type} onChange={handleTypeChange}>
            <option value="ALL">All Competitions</option>
            <option value="Drawing">Drawing</option>
            <option value="Poetry">Poetry</option>
            <option value="Treasure Hunt">Treasure Hunt</option>
          </select>
        </div>
      </div>

      {/* Age Dropdown */}
      <div className="control ml-4">
        <label className="label mb-1">Age:</label>
        <div className="select">
          <select value={age} onChange={handleAgeChange}>
            <option value="ALL">All Ages</option>
            <option value="6-8 years old">6-8 years old</option>
            <option value="9-11 years old">9-11 years old</option>
            <option value="12-15 years old">12-15 years old</option>
          </select>
        </div>
      </div>
    </div>
  );
};

export default FilterBar;
