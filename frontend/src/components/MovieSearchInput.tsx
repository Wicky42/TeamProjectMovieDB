import React from 'react';

interface MovieSearchInputProps {
  label: string;
  placeholder: string;
  setSearchInput: React.Dispatch<React.SetStateAction<string>>;
}

const MovieSearchInput: React.FC<MovieSearchInputProps> = ({ label, placeholder, setSearchInput }) => {
  return (
    <>
      <label
        htmlFor="movie-search"
        style={{
          color: 'var(--accent-strong',
          display: 'block',
          fontSize: '18px',
          fontWeight: 'bold',
          marginTop: '1rem',
          textAlign: 'center',
        }}
      >
        {label}
      </label>
      <input
        onChange={e => setSearchInput(e.target.value)}
        type="text"
        placeholder={placeholder}
        style={{
          display: 'block',
          fontSize: '16px',
          margin: '1rem auto 2rem auto',
          padding: '12px',
          width: '90%',
        }}
      />
    </>
  );
};

export default MovieSearchInput;
