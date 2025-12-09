import React, { useState, useEffect } from "react";
import { Search, Bell, User } from "lucide-react";

export const Navbar: React.FC<{
  onSearch: () => void;
  onNotifications: () => void;
  onUser: () => void;
}> = ({ onSearch, onNotifications, onUser }) => {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 0);
    };
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  // Scroll to section by id
  const scrollToSection = (id: string) => {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: "smooth", block: "start" });
  };

  return (
    <nav
      className={`fixed top-0 w-full z-50 transition-colors duration-300 ${
        isScrolled
          ? "bg-netflix-black"
          : "bg-gradient-to-b from-black/80 to-transparent"
      }`}
    >
      <div className="flex items-center justify-between px-4 md:px-16 py-4">
        <div className="flex items-center gap-8">
          <h1
            className="text-3xl font-bold text-netflix-primary cursor-pointer"
            onClick={() => scrollToSection("inicio")}
          >
            JAVAFLIX
          </h1>
          <ul className="hidden md:flex gap-4 text-sm text-gray-300">
            <li
              className="hover:text-white cursor-pointer transition"
              onClick={() => scrollToSection("inicio")}
            >
              Início
            </li>
            <li
              className="hover:text-white cursor-pointer transition"
              onClick={() => scrollToSection("series")}
            >
              Séries
            </li>
            <li
              className="hover:text-white cursor-pointer transition"
              onClick={() => scrollToSection("filmes")}
            >
              Filmes
            </li>
            <li
              className="hover:text-white cursor-pointer transition"
              onClick={() => scrollToSection("bombando")}
            >
              Bombando
            </li>
            <li
              className="hover:text-white cursor-pointer transition"
              onClick={() => scrollToSection("minha-lista")}
            >
              Minha Lista
            </li>
          </ul>
        </div>

        <div className="flex items-center gap-6 text-white">
          <Search
            className="w-5 h-5 cursor-pointer hover:text-gray-300"
            onClick={onSearch}
          />
          <Bell
            className="w-5 h-5 cursor-pointer hover:text-gray-300"
            onClick={onNotifications}
          />
          <div
            className="flex items-center gap-2 cursor-pointer"
            onClick={onUser}
          >
            <div className="w-8 h-8 rounded bg-yellow-500 flex items-center justify-center text-black font-bold">
              U
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};
