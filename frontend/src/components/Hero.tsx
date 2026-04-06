import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Play, Info } from "lucide-react";
import { getCatalogo } from "../services/api";
import type { Conteudo } from "../types";

interface HeroContent {
  id: string;
  title: string;
  description: string;
  backgroundImage: string;
}

// Fallback content if API fails
const fallbackContents: HeroContent[] = [
  {
    id: "1",
    title: "Stranger Things",
    description:
      "Um grupo de crianças enfrenta forças sobrenaturais e experimentos secretos do governo quando um amigo desaparece misteriosamente.",
    backgroundImage: "/Movies and Series/stranger-things.jpg",
  },
  {
    id: "2",
    title: "Inception",
    description:
      "Um ladrão que rouba segredos corporativos através da tecnologia de compartilhamento de sonhos recebe a tarefa inversa de plantar uma ideia.",
    backgroundImage: "/Movies and Series/inception.jpg",
  },
  {
    id: "3",
    title: "Matrix",
    description:
      "Um programador de computador descobre que a realidade não é o que parece e se junta a uma rebelião contra as máquinas.",
    backgroundImage: "/Movies and Series/matrix.jpg",
  },
];

export const Hero: React.FC = () => {
  const navigate = useNavigate();
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isTransitioning, setIsTransitioning] = useState(false);
  const [heroContents, setHeroContents] = useState<HeroContent[]>(fallbackContents);

  // Fetch real content from API
  useEffect(() => {
    const fetchContent = async () => {
      try {
        const catalogo = await getCatalogo();
        console.log("Catálogo recebido:", catalogo);
        
        if (catalogo && catalogo.length > 0) {
          // Lista de conteúdos que têm vídeos de demonstração
          const conteudosComVideo = [
            "Matrix",
            "Inception",
            "Breaking Bad",
            "Stranger Things"
          ];
          
          // Filtra apenas conteúdos que têm vídeos
          const catalogoComVideo = catalogo.filter((item: Conteudo) =>
            conteudosComVideo.includes(item.titulo)
          );
          
          console.log("Conteúdos com vídeo:", catalogoComVideo);
          
          // Map filtered content to hero content
          const mappedContent = catalogoComVideo.map((item: Conteudo) => {
            return {
              id: item.titulo,
              title: item.titulo,
              description: getDescription(item),
              backgroundImage: getThumbnail(item),
            };
          });
          
          console.log("Hero contents mapeados:", mappedContent);
          setHeroContents(mappedContent);
          setCurrentIndex(0);
        }
      } catch (error) {
        console.error("Error fetching hero content:", error);
        // Keep fallback content
      }
    };
    fetchContent();
  }, []);

  // Helper to get description
  const getDescription = (item: Conteudo): string => {
    const descriptions: Record<string, string> = {
      "O Poderoso Chefao": "A saga da família Corleone, uma das mais poderosas famílias do crime organizado de Nova York.",
      "Shrek": "Um ogro ranzinza embarca em uma jornada para resgatar uma princesa e recuperar seu pântano.",
      "Matrix": "Um programador de computador descobre que a realidade não é o que parece e se junta a uma rebelião contra as máquinas.",
      "Inception": "Dom Cobb é um ladrão com a rara habilidade de roubar segredos do inconsciente, obtidos durante o estado de sono. Impedido de retornar para sua família, ele recebe a oportunidade de se redimir ao realizar uma tarefa aparentemente impossível: plantar uma ideia na mente de alguém.",
      "Breaking Bad": "Um professor de química com câncer se torna fabricante de metanfetamina para garantir o futuro financeiro de sua família.",
      "Stranger Things": "Um grupo de crianças enfrenta forças sobrenaturais e experimentos secretos do governo quando um amigo desaparece misteriosamente.",
      "La Casa de Papel": "Um grupo de criminosos executa o maior assalto da história da Espanha.",
      "Dark": "Uma série de eventos sobrenaturais conecta quatro famílias em uma pequena cidade alemã.",
    };
    return descriptions[item.titulo] || `Assista ${item.titulo} - ${item.genero}`;
  };

  // Helper to get thumbnail
  const getThumbnail = (item: Conteudo): string => {
    const thumbnails: Record<string, string> = {
      "O Poderoso Chefao": "/Movies and Series/GodFather.jpg",
      "Shrek": "/Movies and Series/stranger-things.jpg", // Temporário até ter imagem do Shrek
      "Matrix": "/Movies and Series/matrix.jpg",
      "Inception": "/Movies and Series/inception.jpg",
      "Breaking Bad": "/Movies and Series/breaking-bad.jpg",
      "Stranger Things": "/Movies and Series/stranger-things.jpg",
      "La Casa de Papel": "/Movies and Series/la-casa-de-papel.jpg",
      "Dark": "/Movies and Series/dark.jpg",
    };
    return thumbnails[item.titulo] || "/Movies and Series/stranger-things.jpg";
  };

  useEffect(() => {
    // Só inicia o carrossel se houver conteúdo
    if (heroContents.length === 0) return;

    const interval = setInterval(() => {
      setIsTransitioning(true);

      setTimeout(() => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % heroContents.length);
        setIsTransitioning(false);
      }, 500);
    }, 5000);

    return () => clearInterval(interval);
  }, [heroContents]); // Depende do array completo, não só do length

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
          <button
            onClick={() => {
              console.log("Navegando para título:", currentContent.title);
              navigate(`/watch/${encodeURIComponent(currentContent.title)}`);
            }}
            className="flex items-center gap-2 px-8 py-3 bg-netflix-primary text-black font-bold rounded hover:bg-yellow-400 transition transform hover:scale-105"
          >
            <Play className="w-6 h-6 fill-black" /> Assistir
          </button>
          <button 
            onClick={() => alert(`Mais informações sobre: ${currentContent.title}\n\n${currentContent.description}`)}
            className="flex items-center gap-2 px-8 py-3 bg-gray-500/70 text-white font-bold rounded hover:bg-gray-500/50 transition backdrop-blur-sm transform hover:scale-105"
          >
            <Info className="w-6 h-6" /> Mais Informações
          </button>
        </div>
      </div>
    </div>
  );
};

// Made with Bob
