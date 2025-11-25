export interface AlbumVO {
    id: number,
    albumName: string,
    albumDesc: string,
    albumCover: string,
    userId: number,
    nickname: string,
    createTime: string,
    isDel: number
}

export interface AlbumDTO{
    id: number,
    albumName: string,
    albumDesc: string,
    albumCover: string,
}

export interface Album{
    id: number,
    albumName: string,
    albumDesc: string,
    albumCover: string,
    userId: number,
    nickname: string,
    createTime: string,
    photoList: Photo[]
}

export interface SimpleAlbumDTO{
    id: number,
    albumName: string,
}

export interface Photo{
    id: number,
    photoName: string,
    photoUrl: string,
    albumId: number,
    userId: number,
    nickname: string,
    createTime: string,
    isDel: number
}

export interface PhotoDTO{
    id: number,
    photoName: string,
    photoUrl: string,
    albumId: number,
}

export interface LocalPhotoTempData{
    id: number,
    photoName: string,
    photoUrl: string,
    albumId: number,
    imageFile: string,
    fileName: string
}