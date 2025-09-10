export interface PageResponse<T> {
    total: number;
    page: number;
    pageSize: number;
    data: T[];
}