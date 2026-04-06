export interface Conteudo {
    id?: string;
    titulo: string;
    genero: string;
    classificacao: number;
    tipo: 'Filme' | 'Serie';
    video_url?: string;
    trailer_url?: string;
    duracao_minutos?: number;
}

export interface Filme extends Conteudo {
    tipo: 'Filme';
    duracaoMinutos?: number;
    diretor?: string;
}

export interface Serie extends Conteudo {
    tipo: 'Serie';
    temporadas?: number;
    episodiosPorTemporada?: number;
    minutosPorEpisodio?: number;
}
