import React from "react";
import type { Conteudo } from "../types";

interface RowProps {
  title: string;
  items: Conteudo[];
  onCardClick?: (item: Conteudo) => void;
}

// Real movie/series images mapping
const getImage = (title: string): string => {
  const imageMap: Record<string, string> = {
    // Filmes
    "O Poderoso Chefão":
      "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_FMjpg_UX1000_.jpg",
    Shrek:
      "https://m.media-amazon.com/images/M/MV5BOGZhM2FhNTItODAzNi00YjA0LWEyN2UtNjJlYWQzYzU1MDg5L2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_FMjpg_UX1000_.jpg",
    Matrix:
      "https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_FMjpg_UX1000_.jpg",

    // Séries
    "Breaking Bad":
      "https://m.media-amazon.com/images/M/MV5BYmQ4YWMxYjUtNjZmYi00MDQ1LWFjMjMtNjA5ZDdiYjdiODU5XkEyXkFqcGdeQXVyMTMzNDExODE5._V1_FMjpg_UX1000_.jpg",
    "Stranger Things": "/Movies and Series/stranger-things-2.jpg",
    "La Casa de Papel": "/Movies and Series/la-casa-de-papel.jpg",
    Dark: "/Movies and Series/dark.jpg",
  };

  // Return specific image if found, otherwise return a default movie poster
  return (
    imageMap[title] ||
    "https://images.unsplash.com/photo-1598899134739-24c46f58b8c0?w=500&auto=format&fit=crop&q=60"
  );
};

export const Row: React.FC<RowProps> = ({ title, items, onCardClick }) => {
  const rowRef = React.useRef<HTMLDivElement>(null);
  const isFilmesEmAlta = title === "Filmes em Alta";

  // For Filmes em Alta, enable scroll on mouse wheel and drag
  React.useEffect(() => {
    if (!isFilmesEmAlta || !rowRef.current) return;
    const ref = rowRef.current;
    let isDown = false;
    let startX = 0;
    let scrollLeft = 0;

    const onWheel = (e: WheelEvent) => {
      if (Math.abs(e.deltaX) > Math.abs(e.deltaY)) {
        ref.scrollLeft += e.deltaX;
        e.preventDefault();
      }
    };
    const onMouseDown = (e: MouseEvent) => {
      isDown = true;
      startX = e.pageX - ref.offsetLeft;
      scrollLeft = ref.scrollLeft;
      ref.classList.add("cursor-grabbing");
    };
    const onMouseLeave = () => {
      isDown = false;
      ref.classList.remove("cursor-grabbing");
    };
    const onMouseUp = () => {
      isDown = false;
      ref.classList.remove("cursor-grabbing");
    };
    const onMouseMove = (e: MouseEvent) => {
      if (!isDown) return;
      e.preventDefault();
      const x = e.pageX - ref.offsetLeft;
      const walk = (x - startX) * 1.2; // scroll speed
      ref.scrollLeft = scrollLeft - walk;
    };
    ref.addEventListener("wheel", onWheel, { passive: false });
    ref.addEventListener("mousedown", onMouseDown);
    ref.addEventListener("mouseleave", onMouseLeave);
    ref.addEventListener("mouseup", onMouseUp);
    ref.addEventListener("mousemove", onMouseMove);
    return () => {
      ref.removeEventListener("wheel", onWheel);
      ref.removeEventListener("mousedown", onMouseDown);
      ref.removeEventListener("mouseleave", onMouseLeave);
      ref.removeEventListener("mouseup", onMouseUp);
      ref.removeEventListener("mousemove", onMouseMove);
    };
  }, [isFilmesEmAlta]);

  return (
    <div className="mb-8 px-4 md:px-16 group">
      <h2 className="text-xl md:text-2xl font-bold text-white mb-4 hover:text-netflix-primary transition cursor-pointer">
        {title}
      </h2>

      <div className="relative">
        <div
          ref={rowRef}
          className={`flex gap-4 overflow-x-scroll scrollbar-hide scroll-smooth py-4 ${
            isFilmesEmAlta ? "filmes-em-alta-row" : ""
          }`}
          style={{ scrollbarWidth: "none", msOverflowStyle: "none" }}
        >
          {items.map((item, index) => (
            <div
              key={index}
              className={`relative rounded-md overflow-hidden hover:z-50 transition-transform duration-300 cursor-pointer shadow-xl group/card flex-shrink-0 ${
                isFilmesEmAlta
                  ? "min-w-[200px] w-[200px] md:min-w-[260px] md:w-[260px] aspect-[2/3] hover:scale-110"
                  : "min-w-[150px] w-[150px] md:min-w-[180px] md:w-[180px] aspect-[2/3] hover:scale-105"
              }`}
              style={
                isFilmesEmAlta
                  ? {
                      transition: "transform 0.4s cubic-bezier(.34,1.56,.64,1)",
                    }
                  : {}
              }
              onClick={() => onCardClick && onCardClick(item)}
            >
              <img
                src={getImage(item.titulo)}
                alt={item.titulo}
                className="w-full h-full object-cover"
                loading="lazy"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black via-transparent to-transparent opacity-0 group-hover/card:opacity-100 transition-opacity" />
              <div className="absolute bottom-0 left-0 right-0 p-3 bg-gradient-to-t from-black to-transparent transform translate-y-2 group-hover/card:translate-y-0 transition-transform">
                <h3 className="text-white font-bold text-sm mb-1">
                  {item.titulo}
                </h3>
                <div className="flex items-center gap-2">
                  <p className="text-xs text-green-400 font-bold">
                    {item.classificacao === 0
                      ? "Livre"
                      : `+${item.classificacao}`}
                  </p>
                  <span className="text-xs text-gray-400">•</span>
                  <p className="text-xs text-gray-400">{item.tipo}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
