import React, { useState, useEffect } from "react";
import { Play, Info } from "lucide-react";

interface HeroContent {
  title: string;
  description: string;
  backgroundImage: string;
}

const heroContents: HeroContent[] = [
  {
    title: "Stranger Things",
    description:
      "Um grupo de crianças enfrenta forças sobrenaturais e experimentos secretos do governo quando um amigo desaparece misteriosamente.",
    backgroundImage: "/Movies and Series/stranger-things.jpg",
  },
  {
    title: "Inception",
    description:
      "Um ladrão que rouba segredos corporativos através da tecnologia de compartilhamento de sonhos recebe a tarefa inversa de plantar uma ideia.",
    backgroundImage: "/Movies and Series/inception.jpg",
  },
  {
    title: "Matrix",
    description:
      "Um programador de computador descobre que a realidade não é o que parece e se junta a uma rebelião contra as máquinas.",
    backgroundImage: "/Movies and Series/matrix.jpg",
  },
  {
    title: "O Poderoso Chefão",
    description:
      "O patriarca de uma dinastia do crime organizado transfere o controle de seu império clandestino para seu filho relutante.",
    backgroundImage: "/Movies and Series/GodFather.jpg",
  },
  {
    title: "Breaking Bad",
    description:
      "Um professor de química com câncer se torna fabricante de metanfetamina para garantir o futuro financeiro de sua família.",
    backgroundImage: "/Movies and Series/breaking-bad.jpg",
  },
];

export const Hero: React.FC = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isTransitioning, setIsTransitioning] = useState(false);

  useEffect(() => {
    const interval = setInterval(() => {
      setIsTransitioning(true);

      setTimeout(() => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % heroContents.length);
        setIsTransitioning(false);
      }, 500); // Half second for fade out
    }, 5000); // Change every 5 seconds

    return () => clearInterval(interval);
  }, []);

  const currentContent = heroContents[currentIndex];

  return (
    <div className="relative h-[80vh] w-full overflow-hidden">
      {/* Background Image with Fade Transition */}
      <div
        className={`absolute inset-0 bg-cover bg-center transition-opacity duration-1000 ${
          isTransitioning ? "opacity-0" : "opacity-100"
        }`}
        style={{
          backgroundImage: `url('${currentContent.backgroundImage}')`,
          backgroundSize: "cover",
        }}
      >
        {/* Black gradient overlay */}
        <div className="absolute inset-0 bg-gradient-to-t from-netflix-black via-black/60 to-black/40" />
        <div className="absolute inset-0 bg-black/30" />
      </div>

      {/* Content with Fast Fade Transition */}
      <div
        className={`absolute bottom-[20%] left-4 md:left-16 max-w-xl z-10 transition-opacity duration-500 ${
          isTransitioning ? "opacity-0" : "opacity-100"
        }`}
      >
        <h1 className="text-5xl md:text-7xl font-bold text-white mb-4 drop-shadow-2xl">
          {currentContent.title}
        </h1>
        <p className="text-lg text-gray-200 mb-6 drop-shadow-lg">
          {currentContent.description}
        </p>

        <div className="flex gap-4">
          <button className="flex items-center gap-2 px-8 py-3 bg-netflix-primary text-black font-bold rounded hover:bg-yellow-400 transition transform hover:scale-105">
            <Play className="w-6 h-6 fill-black" /> Assistir
          </button>
          <button className="flex items-center gap-2 px-8 py-3 bg-gray-500/70 text-white font-bold rounded hover:bg-gray-500/50 transition backdrop-blur-sm transform hover:scale-105">
            <Info className="w-6 h-6" /> Mais Informações
          </button>
        </div>
      </div>
    </div>
  );
};
