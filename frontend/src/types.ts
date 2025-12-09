export interface Conteudo {
    titulo: string;
    genero: string;
    classificacao: number;
    tipo: 'Filme' | 'Serie';
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
