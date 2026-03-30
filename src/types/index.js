// Auth types
/** @typedef {'ROLE_ADMIN' | 'ROLE_TEACHER' | 'ROLE_STUDENT'} UserRole */

/**
 * @typedef {Object} User
 * @property {number} id
 * @property {string} email
 * @property {string} fullName
 * @property {UserRole} role
 * @property {string} avatarUrl
 * @property {boolean} isActive
 * @property {string} createdAt
 */

/**
 * @typedef {Object} AuthTokens
 * @property {string} accessToken
 * @property {string} refreshToken
 * @property {User} user
 */

/**
 * @typedef {Object} PageResponse
 * @property {any[]} content
 * @property {number} totalElements
 * @property {number} totalPages
 * @property {number} number
 * @property {number} size
 */

export {}
