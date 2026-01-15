export interface Province {
    code: string;
    name: string;
    administrativeRegionName: string;
}

export interface Ward {
    code: string;
    name: string;
    provinceName: string;
}
