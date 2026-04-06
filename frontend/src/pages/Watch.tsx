import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { VideoPlayer } from '../components/VideoPlayer';
import { ArrowLeft, ThumbsUp, ThumbsDown, Plus } from 'lucide-react';
import { getCatalogo } from '../services/api';
import type { Conteudo, Filme, Serie } from '../types';

export const Watch = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [conteudo, setConteudo] = useState<Conteudo | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchConteudo = async () => {
      const catalogo = await getCatalogo();
      // Decodifica o título da URL e busca no catálogo
      const decodedTitle = decodeURIComponent(id || '');
      console.log('Buscando conteúdo com título:', decodedTitle);
      const item = catalogo.find((c: any) => c.titulo === decodedTitle);
      console.log('Conteúdo encontrado:', item);
      setConteudo(item || null);
      setLoading(false);
    };
    fetchConteudo();
  }, [id]);

  if (loading) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center">
        <div className="text-white text-2xl">Carregando...</div>
      </div>
    );
  }

  if (!conteudo) {
    return (
      <div className="min-h-screen bg-black flex flex-col items-center justify-center gap-4">
        <div className="text-white text-2xl">Conteúdo não encontrado</div>
        <button
          onClick={() => navigate('/browse')}
          className="px-6 py-3 bg-netflix-primary text-black font-bold rounded hover:bg-yellow-400 transition"
        >
          Voltar ao Catálogo
        </button>
      </div>
    );
  }

  // Helper to get demo video URLs
  const getDemoVideoUrl = (titulo: string): string => {
    const demoVideos: Record<string, string> = {
      "O Poderoso Chefão": "https://www.youtube.com/watch?v=sY1S34973zA",
      "Shrek": "https://www.youtube.com/watch?v=CwXOrWvPBPk",
      "Matrix": "https://www.youtube.com/watch?v=m8e-FF8MsqU",
      "Inception": "https://www.youtube.com/watch?v=8hP9D6kZseM",
      "Breaking Bad": "https://www.youtube.com/watch?v=XZ8daibM3AE",
      "Stranger Things": "https://www.youtube.com/watch?v=b9EkMc79ZSU",
      "La Casa de Papel": "https://www.youtube.com/watch?v=_InqQJRqGW4",
      "Dark": "https://www.youtube.com/watch?v=rrwycJ08PSA",
    };
    return demoVideos[titulo] || "";
  };

  // Get video URL (prefer video_url, fallback to trailer_url, then demo)
  const videoUrl = (conteudo as any).video_url || (conteudo as any).trailer_url || getDemoVideoUrl(conteudo.titulo);

  if (!videoUrl) {
    return (
      <div className="min-h-screen bg-black flex flex-col items-center justify-center gap-4">
        <div className="text-white text-2xl">Vídeo não disponível</div>
        <p className="text-gray-400">Este conteúdo ainda não possui um vídeo associado.</p>
        <button
          onClick={() => navigate('/browse')}
          className="px-6 py-3 bg-netflix-primary text-black font-bold rounded hover:bg-yellow-400 transition"
        >
          Voltar ao Catálogo
        </button>
      </div>
    );
  }

  const isFilme = conteudo.tipo === 'Filme';
  const isSerie = conteudo.tipo === 'Serie';

  return (
    <div className="min-h-screen bg-black">
      {/* Back Button */}
      <button
        onClick={() => navigate('/browse')}
        className="absolute top-4 left-4 z-50 bg-black/50 hover:bg-black/70 text-white p-3 rounded-full transition"
      >
        <ArrowLeft className="w-6 h-6" />
      </button>

      {/* Video Player - Compact size */}
      <div className="w-full max-w-5xl mx-auto px-4 pt-16">
        <div className="aspect-video w-full">
          <VideoPlayer
            url={videoUrl}
            title={conteudo.titulo}
            onProgress={(progress) => {
              // TODO: Salvar progresso no backend
              console.log('Progress:', progress);
            }}
            onEnded={() => {
              // TODO: Próximo episódio ou voltar
              console.log('Video ended');
            }}
          />
        </div>
      </div>

      {/* Content Info */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        {/* Title and Basic Info */}
        <div className="mb-6">
          <h1 className="text-2xl md:text-3xl font-bold text-white mb-3">
            {conteudo.titulo}
          </h1>
          
          <div className="flex items-center gap-3 mb-4 flex-wrap text-sm">
            <span className="px-2 py-1 bg-green-600 text-white font-bold rounded">
              {conteudo.classificacao === 0
                ? 'Livre'
                : `+${conteudo.classificacao}`}
            </span>
            <span className="text-gray-300">{conteudo.tipo}</span>
            <span className="text-gray-400">•</span>
            <span className="text-gray-300">{conteudo.genero}</span>
            {isFilme && (conteudo as Filme).duracaoMinutos && (
              <>
                <span className="text-gray-400">•</span>
                <span className="text-gray-300">
                  {(conteudo as Filme).duracaoMinutos} min
                </span>
              </>
            )}
            {isSerie && (
              <>
                <span className="text-gray-400">•</span>
                <span className="text-gray-300">
                  {(conteudo as Serie).temporadas} temporadas
                </span>
              </>
            )}
          </div>

          {/* Action Buttons */}
          <div className="flex items-center gap-3 flex-wrap">
            <button className="flex items-center gap-2 bg-white text-black px-5 py-2 rounded font-bold hover:bg-gray-200 transition text-sm">
              <ThumbsUp className="w-4 h-4" />
              Gostei
            </button>
            <button className="flex items-center gap-2 bg-zinc-800 text-white px-5 py-2 rounded font-bold hover:bg-zinc-700 transition text-sm">
              <ThumbsDown className="w-4 h-4" />
              Não Gostei
            </button>
            <button className="flex items-center gap-2 bg-zinc-800 text-white px-5 py-2 rounded font-bold hover:bg-zinc-700 transition text-sm">
              <Plus className="w-4 h-4" />
              Minha Lista
            </button>
          </div>
        </div>

        {/* Two Column Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Content - 2/3 width */}
          <div className="lg:col-span-2 space-y-6">
            {/* Information Section - Updated Layout */}
            <div className="bg-zinc-900 p-6 rounded-lg">
              <h3 className="text-xl font-bold text-white mb-5">Informações</h3>
              <div className="space-y-3.5">
                {isSerie && (
                  <>
                    <div className="flex text-base gap-8">
                      <span className="text-gray-400 min-w-[240px]">Temporadas:</span>
                      <span className="text-white font-medium">{(conteudo as Serie).temporadas || '-'}</span>
                    </div>
                    <div className="flex text-base gap-8">
                      <span className="text-gray-400 min-w-[240px]">Episódios por temporada:</span>
                      <span className="text-white font-medium">{(conteudo as Serie).episodiosPorTemporada || '8'}</span>
                    </div>
                    <div className="flex text-base gap-8">
                      <span className="text-gray-400 min-w-[240px]">Duração do episódio:</span>
                      <span className="text-white font-medium">60 minutos</span>
                    </div>
                  </>
                )}
                {isFilme && (
                  <div className="flex text-base gap-8">
                    <span className="text-gray-400 min-w-[240px]">Duração:</span>
                    <span className="text-white font-medium">{(conteudo as Filme).duracaoMinutos || '-'} minutos</span>
                  </div>
                )}
                <div className="flex text-base gap-8">
                  <span className="text-gray-400 min-w-[240px]">Gênero:</span>
                  <span className="text-white font-medium">{conteudo.genero}</span>
                </div>
                <div className="flex text-base gap-8">
                  <span className="text-gray-400 min-w-[240px]">Tipo:</span>
                  <span className="text-white font-medium">{conteudo.tipo}</span>
                </div>
                <div className="flex text-base gap-8">
                  <span className="text-gray-400 min-w-[240px]">Classificação:</span>
                  <span className="text-white font-medium">
                    {conteudo.classificacao === 0
                      ? 'Livre para todos os públicos'
                      : `Não recomendado para menores de ${conteudo.classificacao} anos`}
                  </span>
                </div>
              </div>
            </div>

            {/* Episodes Section (for Series) */}
            {isSerie && (
              <div className="bg-zinc-900 p-5 rounded-lg">
                <h3 className="text-lg font-bold text-white mb-3">Episódios</h3>
                <p className="text-gray-400 text-sm">
                  Lista de episódios em breve...
                </p>
              </div>
            )}
          </div>

          {/* Sidebar - 1/3 width */}
          <div className="lg:col-span-1">
            <div className="bg-zinc-900 p-5 rounded-lg">
              <h3 className="text-lg font-bold text-white mb-3">
                Conteúdos Relacionados
              </h3>
              <p className="text-gray-400 text-sm">
                Recomendações personalizadas em breve...
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// Made with Bob
